package com.pishchalova.committee.command.entrant;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pishchalova.committee.util.helper.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeregisterEntrantForFacultyCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(DeregisterEntrantForFacultyCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            if (SessionHelper.isAdmin(request)) {
                LOGGER.log(Level.ERROR, "There is a problem while deregister entrant from faculty because of not allowed access!");
                response.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while deregister entrant from faculty because of checking role of client! ", e);
        }
        EntrantService entrantService = new EntrantService();
        FacultyService facultyService = new FacultyService();
        Integer userId = (Integer) request.getServletContext().getAttribute(request.getSession().getId());
        LOGGER.log(Level.INFO, "Trying to deregister entrant for faculty! ");
        Entrant entrant;
        try {
            entrant = entrantService.filterEntrantsByUserId(userId);
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while deregister entrant from faculty because of filtering entrants by userId", e);
        }
        if (entrant == null) {
            LOGGER.log(Level.ERROR, "There is no entrant while filtering entrant by userId!");
            response.setStatus(MessagesParameterConst.ENTRANT_NOT_FOUND);
            return CommandParameterConst.EMPTY_STRING;
        }
        Faculty faculty;
        try {
            faculty = facultyService.findFacultyById(entrant.getFacultyId());
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while deregister entrant from faculty because of filtering faculty by id", e);
        }
        if (faculty == null) {
            LOGGER.log(Level.ERROR, "Problem with finding faculty by id!");
            response.setStatus(MessagesParameterConst.FACULTY_NOT_FOUND);
            return CommandParameterConst.EMPTY_STRING;
        }
        faculty.setAmountEntrant(faculty.getAmountEntrant() - 1);
        try {
            boolean flag = facultyService.updateFaculty(faculty);
            if (!flag) {
                LOGGER.log(Level.ERROR, "Problem with updating faculty!");
                response.setStatus(MessagesParameterConst.PROBLEM_WITH_UPDATING_FACULTY_INFORMATION);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while deregister entrant from faculty because of updating faculty", e);
        }
        entrant.setFacultyId(0);
        entrant.setStatus(Entrant.Status.FREE);
        try {
            boolean flag = entrantService.updateEntrant(entrant);
            if (!flag) {
                LOGGER.log(Level.ERROR, "Problem with updating entrant!");
                response.setStatus(MessagesParameterConst.PROBLEM_WITH_UPDATING_ENTRANT_INFORMATION);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while deregister entrant from faculty because of updating entrant", e);
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        LOGGER.log(Level.INFO, "Deregister entrant from faculty is completed successfully!");
        return CommandParameterConst.EMPTY_STRING;
    }
}