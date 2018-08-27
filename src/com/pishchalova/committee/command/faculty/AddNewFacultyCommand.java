package com.pishchalova.committee.command.faculty;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.util.helper.*;
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


public class AddNewFacultyCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(AddNewFacultyCommand.class);

    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String doPost(HttpServletRequest req, HttpServletResponse res) throws CommandException {
        try {
            if (!SessionHelper.isAdmin(req)) {
                LOGGER.log(Level.ERROR, "There is a problem while adding new Faculty because of not allowed access!");
                res.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while adding new Faculty because of checking role of client! ", e);
        }

        String body;
        try {
            body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }
        JSONObject jObj = new JSONObject(body);

        LOGGER.log(Level.INFO, "Trying to add new faculty! faculty: " + body);

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
            LOGGER.log(Level.ERROR, "There is a problem with validation of faculty parameters! ");
            res.setStatus(MessagesParameterConst.PROBLEM_WITH_FACULTY_VALIDATION);
            return CommandParameterConst.EMPTY_STRING;
        }
        ArrayList<Integer> subjectsId = new ArrayList<>();
        for (String subject : subjectValues) {
            try {
                subjectsId.add(subjectService.filterSubjectsByName(subject).getId());
            } catch (ServiceException e) {
                throw new CommandException("There is a problem with adding new faculty because of adding new subjects", e);
            }
        }
        Faculty faculty = new Faculty(facultyName, endDateReceiving, facultyEntryPlan, subjectsId);
        FacultyService facultyService = new FacultyService();
        boolean flag;
        try {
            flag = facultyService.addFaculty(faculty);
        } catch (ServiceException e) {
            throw new CommandException("There is a problem with adding new faculty!", e);
        }
        if (!flag) {
            LOGGER.log(Level.ERROR, "There is a problem while creating faculty! " + faculty.toString());
            res.setStatus(MessagesParameterConst.PROBLEM_WITH_CREATION_FACULTY);
            return CommandParameterConst.EMPTY_STRING;
        }
        res.setStatus(HttpServletResponse.SC_ACCEPTED);
        return CommandParameterConst.EMPTY_STRING;
    }
}