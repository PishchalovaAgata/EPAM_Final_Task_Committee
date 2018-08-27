package com.pishchalova.committee.command.entrant;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import com.pishchalova.committee.util.helper.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetEntrantStatusCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(GetEntrantStatusCommand.class);


    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        EntrantService entrantService = new EntrantService();
        UserService userService = new UserService();

        try {
            if (SessionHelper.isAdmin(request)) {
                LOGGER.log(Level.ERROR, "There is a problem while getting entrant status because of not allowed access!");
                response.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting entrant status because of checking role of client! ", e);
        }

        JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
        Integer userId = (Integer) request.getServletContext().getAttribute(request.getSession().getId());
        LOGGER.log(Level.INFO, "Trying to get entrant status by userId: " + userId);

        Entrant entrant;
        User user;
        try {
            user = userService.findUserById(userId);
            if (user == null) {
                LOGGER.log(Level.ERROR, "Problem with finding user by id");
                response.setStatus(MessagesParameterConst.USER_NOT_FOUND);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting information about client because of finding users by userId! ", e);
        }

        try {
            entrant = entrantService.filterEntrantsByUserId(userId);
            if (entrant == null) {
                LOGGER.log(Level.ERROR, "Problem while filtering entrant by userId");
                response.setStatus(MessagesParameterConst.ENTRANT_NOT_FOUND);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting information about client because of filtering entrants by userId! ", e);
        }

        object.put(CommandParameterConst.STATUS, entrant.getStatus().toString().toLowerCase());
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        LOGGER.log(Level.INFO, "Getting entrant status is completed successfully! ");
        return object.toString();
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
