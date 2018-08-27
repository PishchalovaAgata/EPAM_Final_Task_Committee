package com.pishchalova.committee.command.faculty;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.exception.WrongDateFormattingException;
import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.service.FacultyService;
import com.pishchalova.committee.service.SubjectService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class GetFacultyInformationCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(GetFacultyInformationCommand.class);

    @Override
    public String doGet(HttpServletRequest req, HttpServletResponse res) throws CommandException {
        Integer facultyId = CommandHelper.getEntityIdForCommand(req);
        FacultyService facultyService = new FacultyService();
        Faculty faculty;
        try {
            faculty = facultyService.findFacultyById(facultyId);
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting faculty information because of finding faculty by id!", e);
        }
        JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
        if (faculty != null) {
            LOGGER.log(Level.INFO, "Trying to get Information About Faculty. Id:" + facultyId);
            object.put(CommandParameterConst.ID, faculty.getId());
            object.put(CommandParameterConst.NAME, faculty.getFacultyName());
            object.put(CommandParameterConst.ENTRY_PLAN, faculty.getEntryPlan());
            object.put(CommandParameterConst.AMOUNT_ENTRANT, faculty.getAmountEntrant());
            object.put(CommandParameterConst.IS_UNAVAILABLE, faculty.isUnavailable());
            SubjectService subjectService = new SubjectService();
            JSONArray subjectsForFaculty = new JSONArray();
            for (Integer ass : faculty.getSubjectsId()) {
                try {
                    subjectsForFaculty.put(subjectService.filterSubjectsById(ass).getName());
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while getting faculty information because of finding subjects by id!", e);
                }
            }
            object.put(CommandParameterConst.SUBJECTS, subjectsForFaculty);
            try {
                object.put(CommandParameterConst.TIME, faculty.getEndDateReceivingAsLong());
            } catch (WrongDateFormattingException e) {
                throw new CommandException("There is a problem while getting faculty information because of not valid configuration of date into long!", e);
            }
        }
        LOGGER.log(Level.INFO, "Getting Information about faculty is successfully completed! Id: " + facultyId);
        return object.toString();
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
