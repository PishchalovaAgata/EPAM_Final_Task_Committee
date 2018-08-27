package com.pishchalova.committee.command.entrant;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.exception.WrongDateFormattingException;
import com.pishchalova.committee.util.helper.*;


import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import com.pishchalova.committee.service.SubjectService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class getFacultyForEntrantCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(getFacultyForEntrantCommand.class);

    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            if (SessionHelper.isAdmin(request)) {
                LOGGER.log(Level.ERROR, "There is a problem while getting Faculty For Entrant because of not allowed access!");
                response.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting Faculty For Entrant because of checking role of client! ", e);
        }
        EntrantService entrantService = new EntrantService();
        FacultyService facultyService = new FacultyService();
        Integer userId = (Integer) request.getServletContext().getAttribute(request.getSession().getId());

        JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);

        Entrant entrant;
        try {
            entrant = entrantService.filterEntrantsByUserId(userId);
            if (entrant == null) {
                LOGGER.log(Level.ERROR, "Problem while filtering entrants by userId");
                response.setStatus(MessagesParameterConst.ENTRANT_NOT_FOUND);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting faculty because of filtering entrants by userID", e);
        }

        Faculty faculty;
        try {
            faculty = facultyService.findFacultyById(entrant.getFacultyId());
            if (faculty == null) {
                //LOGGER.log(Level.ERROR, "Problem while finding faculty by id!");
                //response.setStatus(MessagesParameterConst.FACULTY_NOT_FOUND);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting faculty because of filtering faculty by ID", e);
        }

        object.put(CommandParameterConst.ID, faculty.getId());
        object.put(CommandParameterConst.ENTRY_PLAN, faculty.getEntryPlan());
        object.put(CommandParameterConst.AMOUNT_ENTRANT, faculty.getAmountEntrant());
        object.put(CommandParameterConst.NAME, faculty.getFacultyName());
        object.put(CommandParameterConst.IS_UNAVAILABLE, faculty.isUnavailable());
        SubjectService subjectService = new SubjectService();
        JSONArray subjectsForFaculty = new JSONArray();
        for (Integer ass : faculty.getSubjectsId()) {
            try {
                String subjectName = subjectService.filterSubjectsById(ass).getName();
                if (subjectName == null) {
                    LOGGER.log(Level.ERROR, "Problem while filtering subjects by Id!");
                    response.setStatus(MessagesParameterConst.SUBJECT_NOT_FOUND);
                    return CommandParameterConst.EMPTY_STRING;
                }
                subjectsForFaculty.put(subjectName);
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while getting faculty for entrant because of filtering subjects by id", e);
            }
        }
        object.put(CommandParameterConst.SUBJECTS, subjectsForFaculty);

        try {
            object.put(CommandParameterConst.TIME, faculty.getEndDateReceivingAsLong());
        } catch (WrongDateFormattingException e) {
            throw new CommandException("There is a problem while getting faculty for entrant because of date formatting", e);
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        LOGGER.log(Level.INFO, "Getting Faculty for entrant is completed successfully!");
        return object.toString();
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}

