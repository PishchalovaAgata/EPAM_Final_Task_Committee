package com.pishchalova.committee.command.user;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.command.subject.ChangeListOfSubjectsCommand;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutUserCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(LogoutUserCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.log(Level.INFO, "Try to logOut user ");
        request.getServletContext().removeAttribute(request.getSession().getId());
        request.getSession().invalidate();
        LOGGER.log(Level.INFO, "Logout was finished successfully!! ");
        return CommandParameterConst.EMPTY_STRING;
    }
}
