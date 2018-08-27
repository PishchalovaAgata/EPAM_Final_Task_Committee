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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class ConfirmRegistrationCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(ConfirmRegistrationCommand.class);

    private static final String WELCOME = "WEB-INF/jsp/welcome.jsp";

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {

        String encryptedEmail = request.getParameter(CommandParameterConst.ENCRYPTED_DATA);

        LOGGER.log(Level.INFO, "Try to get parameter " + CommandParameterConst.ENCRYPTED_DATA + " from request: " + encryptedEmail);
        String decodedEmail = new String(Base64.getDecoder().decode(encryptedEmail), StandardCharsets.UTF_8);
        UserService userService = new UserService();
        User user = null;
        try {
            user = userService.filterUsersByEmail(decodedEmail);
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while confirming registration because of filtering users by email", e);
        }
        if (user == null) {
            response.setStatus(MessagesParameterConst.USER_NOT_FOUND);
            LOGGER.log(Level.ERROR, "There is a problem while confirming registration because of filtering users by email");
            return CommandParameterConst.EMPTY_STRING;
        }
        if (user.getEmail().equals(decodedEmail)) {
            LOG.debug("User with not active status found in database.");
            user.setActivated(true);
            user.setPassword(CommandParameterConst.EMPTY_STRING);
            boolean flag;
            try {
                flag = userService.updateUser(user);
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while confirming registration because of updating user", e);
            }
            if (!flag) {
                response.setStatus(MessagesParameterConst.PROBLEM_WITH_UPDATING_USER_INFORMATION);
                LOGGER.log(Level.ERROR, "There is a problem while confirming registration because of updating user");
                return CommandParameterConst.EMPTY_STRING;
            }
            LOGGER.log(Level.INFO, "User active status is updated");
            return WELCOME;
        } else {
            response.setStatus(MessagesParameterConst.USER_NOT_FOUND);
            LOGGER.log(Level.ERROR, "User not found with such email: " + decodedEmail);
            return CommandParameterConst.EMPTY_STRING;
        }
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}


