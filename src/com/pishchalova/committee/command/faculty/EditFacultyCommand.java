package com.pishchalova.committee.command.faculty;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import com.pishchalova.committee.service.SubjectService;
import com.pishchalova.committee.validation.FacultyValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class EditFacultyCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(EditFacultyCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            if (!SessionHelper.isAdmin(request)) {
                LOGGER.log(Level.ERROR, "There is a problem while editing Faculty because of not allowed access!");
                response.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while editing Faculty because of checking role of client! ", e);
        }
        Integer facultyId = CommandHelper.getEntityIdForCommand(request);
        String body;
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }

        LOG.log(Level.INFO, "Trying to edit faculty with id " + facultyId + " where new values are: " + body);

        JSONObject jObj = new JSONObject(body);

        String facultyName = jObj.getString(CommandParameterConst.NAME);
        Integer facultyEntryPlan = jObj.getInt(CommandParameterConst.ENTRY_PLAN);
        Long endDateReceiving = jObj.getLong(CommandParameterConst.TIME);

        ArrayList<String> subjectValues = new ArrayList<>();
        JSONArray nameSubjects = jObj.getJSONArray(CommandParameterConst.SUBJECTS);

        SubjectService subjectService = new SubjectService();

        for (int i = 0; i < nameSubjects.length(); i++) {
            String subjectName = (String) nameSubjects.get(i);
            subjectValues.add(subjectName);
        }

        boolean valid = FacultyValidation.validateFacultyParameters(facultyName, facultyEntryPlan.toString(), subjectValues);
        if (!valid) {
            LOG.log(Level.INFO, "Data for editing faculty are not valid! Editing wasn't completed!");
            response.setStatus(MessagesParameterConst.PROBLEM_WITH_FACULTY_VALIDATION);
            return CommandParameterConst.EMPTY_STRING;
        }
        FacultyService facultyService = new FacultyService();
        EntrantService entrantService = new EntrantService();
        ArrayList<Integer> subjectsId = new ArrayList<>();
        for (String subject : subjectValues) {
            try {
                subjectsId.add(subjectService.filterSubjectsByName(subject).getId());
            } catch (ServiceException e) {
                throw new CommandException("Problem while editing faculty because of construction list of subjects", e);
            }
        }
        Faculty faculty;
        try {
            if (entrantService.filterEntrantsByFaculty(facultyId) == null) {
                faculty = new Faculty(facultyId, facultyName, endDateReceiving, facultyEntryPlan, subjectsId);
            } else {
                ArrayList<Integer> subjects = facultyService.findFacultyById(facultyId).getSubjectsId();
                if (!subjects.toString().equals(subjectsId.toString())) {
                    LOGGER.log(Level.ERROR, "There is a problem while editing faculty because of enlisted entrants!");
                    response.setStatus(MessagesParameterConst.CANNOT_EDIT_FACULTY_SUBJECTS);
                    return CommandParameterConst.EMPTY_STRING;
                }
                faculty = new Faculty(facultyId,
                        facultyName,
                        endDateReceiving,
                        facultyEntryPlan,
                        facultyService.findFacultyById(facultyId).getAmountEntrant(),
                        facultyService.findFacultyById(facultyId).getSubjectsId());
            }
            boolean flag = facultyService.updateFaculty(faculty);
            if (!flag) {
                LOGGER.log(Level.ERROR, "There is a problem while editing faculty!");
                response.setStatus(MessagesParameterConst.CANNOT_EDIT_FACULTY_SUBJECTS);
                return CommandParameterConst.EMPTY_STRING;
            }
            LOG.log(Level.INFO, "Editing faculty with id: " + facultyId + " was completed successfully! ");

        } catch (ServiceException e) {
            throw new CommandException("There is a problem while editing faculty!", e);
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        return CommandParameterConst.EMPTY_STRING;
    }
}
