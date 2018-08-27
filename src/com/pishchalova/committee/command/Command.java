package com.pishchalova.committee.command;

import com.pishchalova.committee.command.user.LoginUserCommand;
import com.pishchalova.committee.exception.CommandException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Command {
    Logger LOG = LogManager.getLogger(Command.class);

    enum ActionType {
        GET, POST;
    }

    default String execute(HttpServletRequest request, HttpServletResponse response,
                           ActionType actionType) throws CommandException {
        LOG.log(Level.DEBUG, "Start executing Command");
        if (actionType == ActionType.GET) {
            return doGet(request, response);
        } else if (actionType == ActionType.POST) {
            return doPost(request, response);
        }
        throw new CommandException("Not valid ActionType for Command");
    }

    String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException;

    String doPost(HttpServletRequest request, HttpServletResponse response) throws CommandException;

}
