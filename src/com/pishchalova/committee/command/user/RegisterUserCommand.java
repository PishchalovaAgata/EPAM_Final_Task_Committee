package com.pishchalova.committee.command.user;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.UserService;
import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.validation.UserValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class RegisterUserCommand implements Command {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(RegisterUserCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String doPost(HttpServletRequest req, HttpServletResponse res) throws CommandException {
        String body;
        try {
            body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }
        JSONObject jObj = new JSONObject(body);

        String userLogin = jObj.getString(CommandParameterConst.LOGIN);
        String userPassword = jObj.getString(CommandParameterConst.PASSWORD);
        String userEmail = jObj.getString(CommandParameterConst.EMAIL);

        UserService userService = new UserService();

        boolean valid = UserValidation.validateUserParameters(userLogin, userPassword, userEmail);

        if (!valid) {
            res.setStatus(MessagesParameterConst.PROBLEM_WITH_USER_VALIDATION);
            LOGGER.log(Level.ERROR, "Problem with User Validation!!!");
            return CommandParameterConst.EMPTY_STRING;
        } else {
            boolean flag;
            try {
                flag = userService.registerUser(userLogin, userPassword, userEmail);
                MailHelper.sendConfirmationEmail(new User(userLogin, userPassword, userEmail));
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while registering new user! ", e);
            }
            if (!flag) {
                res.setStatus(MessagesParameterConst.PROBLEM_WITH_CREATION_USER);
                LOGGER.log(Level.ERROR, "There is a problem with creation new User!");
                return CommandParameterConst.EMPTY_STRING;
            }
            res.setStatus(HttpServletResponse.SC_ACCEPTED);
            LOG.log(Level.INFO, "User " + userLogin + " was registered successfully!");
        }
        return body;
    }
}