package com.pishchalova.committee.command.entrant;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.util.ParameterConst.MessagesParameterConst;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.exception.WrongDateFormattingException;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import com.pishchalova.committee.util.helper.*;

public class RegisterEntrantForFacultyCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(RegisterEntrantInformationCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        String body;
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }
        try {
            if (SessionHelper.isAdmin(request)) {
                LOGGER.log(Level.ERROR, "There is a problem while registering entrant for faculty because of not allowed access!");
                response.setStatus(MessagesParameterConst.NOT_ALLOWED_ACCESS);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while registering entrant for faculty because of checking role of client! ", e);
        }
        EntrantService entrantService = new EntrantService();
        Integer userId = (Integer) request.getServletContext().getAttribute(request.getSession().getId());
        LOGGER.log(Level.INFO, "Trying register entrant for faculty by userId: " + userId);
        Entrant entrant;
        JSONObject jObj = new JSONObject(body);
        Integer facultyId = jObj.getInt(CommandParameterConst.FACULTY_ID);
        FacultyService facultyService = new FacultyService();

        try {
            entrant = entrantService.filterEntrantsByUserId(userId);
            if (entrant == null) {
                LOGGER.log(Level.INFO, "Problem with filtering entrants by userId!");
                response.setStatus(MessagesParameterConst.ENTRANT_NOT_FOUND);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (ServiceException e) {
            throw new CommandException("There is a problem while registrationInformation about client because of finding entrant by Id ", e);
        }

        try {
            if (entrant.getFacultyId() != 0 || !entrant.getStatus().equals(Entrant.Status.FREE)) {
                LOGGER.log(Level.INFO, "Problem with the fact that entrant is already enlisted or submitted!");
                response.setStatus(MessagesParameterConst.ENTRANT_IS_ALREADY_ENLISTED_OR_SUBMITTED);
                return CommandParameterConst.EMPTY_STRING;
            } else if (facultyService.findFacultyById(facultyId).getEndDateReceivingAsLong() <= System.currentTimeMillis()) {
                LOGGER.log(Level.INFO, "Problem with the fact that enrollment is over!");
                response.setStatus(MessagesParameterConst.ENROLLMENT_FOR_FACULTY_IS_OVER);
                return CommandParameterConst.EMPTY_STRING;
            }
        } catch (WrongDateFormattingException | ServiceException e) {
            throw new CommandException("There is a problem while registrationInformation about client because of validation for this command ", e);
        }

        entrant.setFacultyId(facultyId);
        entrant.setStatus(Entrant.Status.ENLISTED);
        try {
            entrantService.updateEntrant(entrant);
            Faculty faculty = facultyService.findFacultyById(facultyId);
            faculty.setAmountEntrant(faculty.getAmountEntrant() + 1);
            facultyService.updateFaculty(faculty);
        } catch (ServiceException e) {
            throw new CommandException("THere is a problem while registering entrant for faculty because of updating inside system", e);
        }
        LOGGER.log(Level.INFO, "Registering entrant for faculty is successfully completed!");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        return CommandParameterConst.EMPTY_STRING;
    }
}
