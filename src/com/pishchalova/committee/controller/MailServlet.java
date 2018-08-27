package com.pishchalova.committee.controller;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.command.CommandType;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "MailServlet", urlPatterns = "/mail/*")
public class MailServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(MailServlet.class);

    private static final String HYPHEN = "-";
    private static final String UNDERSCORE = "_";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res, Command.ActionType.GET);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res, Command.ActionType.POST);
    }

    private void process(HttpServletRequest request, HttpServletResponse response, Command.ActionType actionType) throws IOException, ServletException {
        String commandName = request.getParameter(CommandParameterConst.COMMAND);
        LOGGER.log(Level.INFO, "Try to execute command with name: " + commandName);
        Command command = CommandType.of(commandName.toUpperCase().replaceAll(HYPHEN, UNDERSCORE));
        try {
            String data = command.execute(request, response, actionType);
            request.getRequestDispatcher(data).forward(request, response);
        } catch (CommandException e) {
            e.printStackTrace();
        }
        response.getWriter().close();
    }
}
