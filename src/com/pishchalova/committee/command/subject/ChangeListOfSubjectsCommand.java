package com.pishchalova.committee.command.subject;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Subject;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.service.SubjectService;
import com.pishchalova.committee.validation.SubjectValidation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class ChangeListOfSubjectsCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(ChangeListOfSubjectsCommand.class);

    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String doPost(HttpServletRequest req, HttpServletResponse res) throws CommandException {
        try {
            if (!SessionHelper.isAdmin(req)) {
                LOGGER.log(Level.ERROR, "There is a problem while changing list of subjects because of not allowed access ");
                res.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while changing list of subjects because of checking the role of client", e);
        }
        String body;
        try {
            body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }
        JSONObject jObj = new JSONObject(body);

        JSONArray addSubjects = jObj.getJSONArray(CommandParameterConst.ADD_SUBJECT);
        JSONArray editSubjects = jObj.getJSONArray(CommandParameterConst.EDIT_SUBJECT);
        JSONArray deleteSubjects = jObj.getJSONArray(CommandParameterConst.DELETE_SUBJECT);

        try {
            SubjectService subjectService = new SubjectService();
            if (addSubjects.length() != 0) {
                LOGGER.log(Level.INFO, "Try to add new subjects! " + addSubjects.toString());
                for (int i = 0; i < addSubjects.length(); i++) {
                    String subjectName = (String) addSubjects.get(i);
                    if (SubjectValidation.validateSubjectParameters(subjectName)) {
                        Subject subject = new Subject(subjectName);
                        boolean flag = subjectService.addSubject(subject);
                        if (!flag) {
                            LOGGER.log(Level.ERROR, "There is a problem while creating subject!" + subject.toString());
                            res.setStatus(MessagesParameterConst.PROBLEM_WITH_CREATION_SUBJECT);
                        }
                    } else {
                        LOGGER.log(Level.ERROR, "There is a problem while creating subject because of subjects validation!");
                        res.setStatus(MessagesParameterConst.PROBLEM_WITH_SUBJECT_VALIDATION);
                    }
                }
                LOGGER.log(Level.INFO, "Adding new subjects is completed! ");
            }
            if (editSubjects.length() != 0) {
                LOGGER.log(Level.INFO, "Try to edit subjects! " + editSubjects.toString());
                for (int i = 0; i < editSubjects.length(); i++) {
                    JSONObject jsonObject = editSubjects.getJSONObject(i);
                    String oldValue = jsonObject.getString(CommandParameterConst.OLD_SUBJECT);
                    String newValue = jsonObject.getString(CommandParameterConst.NEW_SUBJECT);
                    if (SubjectValidation.validateSubjectParameters(newValue)) {
                        Subject subject = new Subject(newValue);
                        subject.setId(subjectService.filterSubjectsByName(oldValue).getId());
                        boolean flag = subjectService.updateSubject(subject);
                        if (!flag) {
                            LOGGER.log(Level.ERROR, "There is a problem while editing subject!" + subject.toString());
                            res.setStatus(MessagesParameterConst.PROBLEM_WITH_UPDATING_SUBJECT_INFORMATION);
                            return CommandParameterConst.EMPTY_STRING;
                        }
                    } else {
                        LOGGER.log(Level.ERROR, "There is a problem while editing subject because of subjects validation!");
                        res.setStatus(MessagesParameterConst.PROBLEM_WITH_SUBJECT_VALIDATION);
                    }
                }
                LOGGER.log(Level.INFO, "Editing subjects is completed! ");
            }
            if (deleteSubjects.length() != 0) {
                LOGGER.log(Level.INFO, "Try to delete subjects! " + deleteSubjects.toString());
                for (int i = 0; i < deleteSubjects.length(); i++) {
                    String subjectName = (String) deleteSubjects.get(i);
                    Subject subject = subjectService.filterSubjectsByName(subjectName);
                    boolean flag = subjectService.deleteSubject(subject.getId());
                    if (!flag) {
                        LOGGER.log(Level.ERROR, "There is a problem while deleting subject!" + subject.toString());
                        res.setStatus(MessagesParameterConst.PROBLEM_WITH_DELETING_SUBJECT);
                    }
                }
                LOGGER.log(Level.INFO, "Deleting subjects is completed! ");
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while changing list of subjects! ", e);
        }
        res.setStatus(HttpServletResponse.SC_ACCEPTED);
        return CommandParameterConst.EMPTY_STRING;
    }
}

