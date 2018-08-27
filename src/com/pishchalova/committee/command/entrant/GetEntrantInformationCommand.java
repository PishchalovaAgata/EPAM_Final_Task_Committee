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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class GetEntrantInformationCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(GetEntrantInformationCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        EntrantService entrantService = new EntrantService();
        UserService userService = new UserService();
        JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
        Integer userId = (Integer) request.getServletContext().getAttribute(request.getSession().getId());
        LOGGER.log(Level.INFO, "Trying to get entrant information! By UserId: " + userId);
        Entrant entrant;
        User user;
        try {
            user = userService.findUserById(userId);
            if (user == null) {
                LOGGER.log(Level.ERROR, "Problem while getting entrant information because of finding user by id! Id:" + userId);
                response.setStatus(MessagesParameterConst.USER_NOT_FOUND);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting information about client because of finding users by userId! ", e);
        }

        object.put(CommandParameterConst.LOGIN, user.getLogin());
        object.put(CommandParameterConst.EMAIL, user.getEmail());

        try {
            entrant = entrantService.filterEntrantsByUserId(userId);
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting information about client because of filtering entrants by userId! ", e);
        }
        if (entrant != null) {
            object.put(CommandParameterConst.CERTIFICATE, entrant.getCertificate());
            object.put(CommandParameterConst.FIRST_NAME, entrant.getEntrantFirstName());
            object.put(CommandParameterConst.SURNAME, entrant.getEntrantSurName());
            JSONArray jsonObjectArrayList = new JSONArray();
            ArrayList<Mark> marks = entrant.getMarks();
            SubjectService subjectService = new SubjectService();
            for (Mark mark : marks) {
                JSONObject jsonObject = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
                String subjectName;
                try {
                    subjectName = subjectService.filterSubjectsById(mark.getSubjectId()).getName();
                    if (subjectName == null) {
                        LOGGER.log(Level.ERROR, "Subject is not found! ");
                        response.setStatus(MessagesParameterConst.SUBJECT_NOT_FOUND);
                        return CommandParameterConst.EMPTY_STRING;
                    }
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while getting information about client because of filtering subjects for entrants!", e);
                }

                jsonObject.put(CommandParameterConst.SUBJECT, subjectName);
                jsonObject.put(CommandParameterConst.VALUE, mark.getValue());
                jsonObjectArrayList.put(jsonObject);
            }
            object.put(CommandParameterConst.MARKS, jsonObjectArrayList);
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        LOGGER.log(Level.INFO, "Getting entrant Information is completed successfully!");
        return object.toString();
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}

