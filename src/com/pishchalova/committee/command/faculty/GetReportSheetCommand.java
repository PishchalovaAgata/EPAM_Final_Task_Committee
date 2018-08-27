package com.pishchalova.committee.command.faculty;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.util.helper.*;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import com.pishchalova.committee.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class GetReportSheetCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(GetReportSheetCommand.class);

    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            if (!SessionHelper.isAdmin(request)) {
                LOGGER.log(Level.ERROR, "There is a problem while getting reportSheet for Faculty because of not allowed access!");
                response.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting reportSheet for Faculty  because of checking role of client! ", e);
        }

        Integer facultyId = CommandHelper.getEntityIdForCommand(request);
        LOGGER.log(Level.ERROR, "Trying  to get reportSheet for Faculty! Id: " + facultyId);

        FacultyService facultyService = new FacultyService();
        EntrantService entrantService = new EntrantService();
        ArrayList<Integer> entrantsId;
        try {
            entrantsId = entrantService.sortEntrantsByTotalMark(facultyId);
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while getting report for faculty with id: " + facultyId + " because of sorting entrants by total mark!", e);
        }
        JSONObject finalObject = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
        JSONArray jsonObjectArrayList = new JSONArray();
        UserService userService = new UserService();
        if (!entrantsId.isEmpty()) {
            for (Integer entrantId : entrantsId) {
                Entrant entrant;
                try {
                    entrant = entrantService.findEntrantById(entrantId);
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while getting report for faculty with id: " + facultyId + " because of finding entrants by id!", e);
                }
                JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
                object.put(CommandParameterConst.ID, entrantId);
                try {
                    object.put(CommandParameterConst.EMAIL, userService.findUserById(entrant.getUserId()).getEmail());
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while getting report for faculty with id: " + facultyId + " because of finding email of user!", e);
                }
                try {
                    object.put(CommandParameterConst.LOGIN, userService.findUserById(entrant.getUserId()).getLogin());
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while getting report for faculty with id: " + facultyId + " because of finding login of user!", e);

                }
                object.put(CommandParameterConst.FIRST_NAME, entrant.getEntrantFirstName());
                object.put(CommandParameterConst.SURNAME, entrant.getEntrantSurName());
                try {
                    object.put(CommandParameterConst.SCORE, entrantService.getTotalMarkForEntrant(entrant.getEntrantId()));
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while getting report for faculty with id: " + facultyId + " because of finding total mark of entrant!", e);
                }
                object.put(CommandParameterConst.ENROLLED, entrant.getStatus().name().toLowerCase());
                jsonObjectArrayList.put(object);
            }
            finalObject.put(CommandParameterConst.ENTRANTS, jsonObjectArrayList);
            try {
                finalObject.put(CommandParameterConst.FACULTY_NAME, facultyService.findFacultyById(facultyId).getFacultyName());
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while getting report for faculty with id: " + facultyId + " because of finding faculty by id!", e);
            }
        }
        LOGGER.log(Level.INFO, "Getting reportSheet for Faculty is completed successfully! Id: " + facultyId);

        return finalObject.toString();
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
