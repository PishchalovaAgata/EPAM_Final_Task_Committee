package com.pishchalova.committee.command.user;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.stream.Collectors;

public class LoginUserCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(LoginUserCommand.class);

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        String body;
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }
        JSONObject jObj = new JSONObject(body);
        HttpSession session = request.getSession();

        String userLogin = jObj.getString(CommandParameterConst.LOGIN);
        String userPassword = jObj.getString(CommandParameterConst.PASSWORD);
        UserService userService = new UserService();
        User user;
        try {
            user = userService.authorizeUser(userLogin, userPassword);
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while authorising user!", e);
        }
        JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
        if (user != null) {
            object.put(CommandParameterConst.LOGIN, user.getLogin());
            object.put(CommandParameterConst.ROLE, user.getRole());
            request.getServletContext().setAttribute(session.getId(), user.getId());
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            LOGGER.log(Level.INFO, "User " + userLogin + " was authorised successfully!");
        } else {
            response.setStatus(MessagesParameterConst.USER_NOT_FOUND);
            LOGGER.log(Level.INFO, "User " + userLogin + " wasn't authorised! Such user was not found!");
            return CommandParameterConst.EMPTY_STRING;
        }
        return object.toString();
    }

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
