package com.pishchalova.committee.command.subject;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.entity.Subject;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.SubjectService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;


public class ViewAllSubjectsCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(ViewAllSubjectsCommand.class);


    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        LOGGER.log(Level.INFO, "Trying to view all subjects! ");
        SubjectService subjectService = new SubjectService();
        ArrayList<Subject> subjects;
        try {
            subjects = subjectService.getAllSubjects();
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while viewing all subjects! ", e);
        }
        JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
        JSONArray jsonObjectArrayList = new JSONArray();
        if (subjects != null) {
            for (Subject subject : subjects) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CommandParameterConst.ID, subject.getId());
                jsonObject.put(CommandParameterConst.NAME, subject.getName());
                jsonObjectArrayList.put(jsonObject);
            }
            object.put(CommandParameterConst.SUBJECTS, jsonObjectArrayList);
            LOGGER.log(Level.INFO, "Viewing all subjects is completed! Subjects: " + subjects.toString());
        }
        return object.toString();
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}