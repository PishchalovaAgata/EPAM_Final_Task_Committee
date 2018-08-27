package com.pishchalova.committee.command.faculty;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteFacultyCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(DeleteFacultyCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            if (!SessionHelper.isAdmin(request)) {
                LOGGER.log(Level.ERROR, "There is a problem while deleting Faculty because of not allowed access!");
                response.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while deleting Faculty because of checking role of client! ", e);
        }
        Integer facultyId = CommandHelper.getEntityIdForCommand(request);
        FacultyService facultyService = new FacultyService();
        EntrantService entrantService = new EntrantService();
        boolean flag = false;
        try {
            if (entrantService.filterEntrantsByFaculty(facultyId).isEmpty()) {
                flag = facultyService.deleteFaculty(facultyId);
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                LOGGER.log(Level.INFO, "Faculty with id: " + facultyId + " was deleted successfully!");
            }
            if (!flag) {
                LOGGER.log(Level.ERROR, "There is a problem while deleting Faculty because of the fact that there are enlisted entrants!");
                response.setStatus(MessagesParameterConst.PROBLEM_WITH_DELETING_FACULTY);
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while deleting faculty with id " + facultyId, e);
        }
        return CommandParameterConst.EMPTY_STRING;
    }
}
