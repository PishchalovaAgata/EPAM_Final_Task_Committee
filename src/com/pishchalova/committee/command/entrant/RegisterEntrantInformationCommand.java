package com.pishchalova.committee.command.entrant;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Mark;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.SubjectService;
import com.pishchalova.committee.service.UserService;
import com.pishchalova.committee.validation.EntrantValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.pishchalova.committee.util.helper.*;

public class RegisterEntrantInformationCommand implements Command {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(RegisterEntrantInformationCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public String doPost(HttpServletRequest req, HttpServletResponse res) throws CommandException {
        String body;
        try {
            body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }

        LOGGER.log(Level.INFO, "Trying to register entrant information!");
        JSONObject jObj = new JSONObject(body);

        UserService userService = new UserService();
        EntrantService entrantService = new EntrantService();
        Integer userId = (Integer) req.getServletContext().getAttribute(req.getSession().getId());//todo: make better
        System.out.println(userId);
        User user;
        try {
            user = userService.findUserById(userId);
            if (user == null) {
                LOGGER.log(Level.INFO, "Problem with finding user by id! " + userId);
                res.setStatus(MessagesParameterConst.USER_NOT_FOUND);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while editing information about client because of finding users by userId! ", e);
        }

        String userLogin = jObj.getString(CommandParameterConst.LOGIN);
        String userPassword = jObj.getString(CommandParameterConst.PASSWORD).equals(CommandParameterConst.EMPTY_STRING) ? "" : jObj.getString(CommandParameterConst.PASSWORD);
        String userEmail = jObj.getString(CommandParameterConst.EMAIL);
        System.out.println(userPassword);


        User newUser = new User(user.getId(), user.getRole(), userLogin, userPassword, userEmail, user.isActivated());
        try {
            boolean flag = userService.updateUser(newUser);
            if (!flag) {
                LOGGER.log(Level.ERROR, "Problem with updating user!");
                res.setStatus(MessagesParameterConst.PROBLEM_WITH_UPDATING_USER_INFORMATION);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while register information of entrant because of updating user!", e);
        }
        try {
            if (SessionHelper.isAdmin(req)) {
                //LOGGER.log(Level.ERROR, "There is a problem while registering entrant information because of not allowed access!");
                //res.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while registering entrant information because of checking role of client! ", e);
        }
        String entrantFirstName = jObj.getString(CommandParameterConst.FIRST_NAME);
        String entrantSurName = jObj.getString(CommandParameterConst.SURNAME);
        Integer certificate = jObj.getInt(CommandParameterConst.CERTIFICATE);
        ArrayList<Mark> marks = new ArrayList<>();
        ArrayList<Integer> markValues = new ArrayList<>();
        JSONArray subjects = jObj.getJSONArray(CommandParameterConst.MARKS);
        SubjectService subjectService = new SubjectService();
        for (int i = 0; i < subjects.length(); i++) {
            String subjectName = subjects.getJSONObject(i).getString(CommandParameterConst.SUBJECT);
            Integer markValue = subjects.getJSONObject(i).getInt(CommandParameterConst.VALUE);
            markValues.add(markValue);
            try {
                marks.add(new Mark(subjectService.filterSubjectsByName(subjectName).getId(), markValue));
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while register information of entrant because of filtering subjects by name!", e);
            }
        }
        boolean valid = EntrantValidation.validateEntrantParameters(entrantFirstName, entrantSurName, certificate.toString(), markValues);

        if (!valid) {
            LOGGER.log(Level.INFO, "Problem with entrant validation! " + userId);
            res.setStatus(MessagesParameterConst.PROBLEM_WITH_ENTRANT_VALIDATION);
            return CommandParameterConst.EMPTY_STRING;
        }
        try {
            if (entrantService.filterEntrantsByUserId(userId) == null) {
                Entrant entrant = new Entrant(entrantFirstName, entrantSurName, certificate,
                        userService.filterUsersByLogin(userLogin).getId(), marks, Entrant.Status.FREE);
                boolean flag = entrantService.addEntrant(entrant);
                if (!flag) {
                    LOGGER.log(Level.ERROR, "Problem with creation entrant!");
                    res.setStatus(MessagesParameterConst.PROBLEM_WITH_CREATION_ENTRANT);
                    return CommandParameterConst.EMPTY_STRING;
                }
            } else {
                Entrant entrant = entrantService.filterEntrantsByUserId(userId);
                entrant.setEntrantFirstName(entrantFirstName);
                entrant.setEntrantSurName(entrantSurName);
                entrant.setCertificate(certificate);
                entrant.setMarks(marks);
                boolean flag = entrantService.updateEntrant(entrant);
                if (!flag) {
                    LOGGER.log(Level.ERROR, "Problem with updating entrant!");
                    res.setStatus(MessagesParameterConst.PROBLEM_WITH_UPDATING_ENTRANT_INFORMATION);
                    return CommandParameterConst.EMPTY_STRING;
                }
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while register information of entrant because of updating information about entrant!", e);
        }
        LOGGER.log(Level.INFO, "Registering entrant information is successfully completed!");
        res.setStatus(HttpServletResponse.SC_ACCEPTED);
        return CommandParameterConst.EMPTY_STRING;
    }
}
