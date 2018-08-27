package com.pishchalova.committee.controller;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.command.Command.ActionType;
import com.pishchalova.committee.command.CommandType;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@WebServlet(name = "MainServlet", urlPatterns = "/committee/*")
public class MainServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(MainServlet.class);
    private static final long serialVersionUID = 1L;
    private static final String SLASH = "/";
    private static final String UNDERSCORE = "_";
    private static final String NUMBER_REGEX = "_[0-9]+";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setHeader("Content-Type", "application/json");
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        super.service(req, res);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        process(req, res, ActionType.GET);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        process(req, res, ActionType.POST);
    }

    private void process(HttpServletRequest request, HttpServletResponse response, ActionType actionType) throws IOException {
        int ignoreAmt = request.getContextPath().length() + request.getServletPath().length();
        String stringRequestCommand = request.getRequestURI().substring(ignoreAmt + 1);
        String commandName = stringRequestCommand.toUpperCase().replaceAll(SLASH, UNDERSCORE).replaceAll(NUMBER_REGEX, CommandParameterConst.EMPTY_STRING);
        LOGGER.log(Level.INFO, "Try to execute command with name: " + commandName);
        Command command = CommandType.of(commandName);
        try {
            String data = command.execute(request, response, actionType);
            LOGGER.log(Level.INFO, "Gotten information from command: DATA: " + data);
            response.getWriter().write(data);
        } catch (CommandException e) {
            e.printStackTrace();
        }
        response.getWriter().close();
    }

}
