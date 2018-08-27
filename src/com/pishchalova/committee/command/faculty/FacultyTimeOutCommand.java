package com.pishchalova.committee.command.faculty;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;


public class FacultyTimeOutCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(FacultyTimeOutCommand.class);

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        EntrantService entrantService = new EntrantService();
        FacultyService facultyService = new FacultyService();
        ArrayList<Integer> facultyIds;
        try {
            facultyIds = facultyService.filterFacultiesByDate(System.currentTimeMillis());
        } catch (ServiceException e) {
            throw new CommandException("Problem with faculty real time actions because of filtering faculties by date!", e);
        }
        if (facultyIds.isEmpty()) {
            return CommandParameterConst.EMPTY_STRING;
        }
        for (Integer facultyId : facultyIds) {
            LOGGER.log(Level.INFO, "Trying to edit faculty with id " + facultyId);
            Faculty faculty;
            try {
                faculty = facultyService.findFacultyById(facultyId);
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while facultyTimeOutCommand because of finding faculty with id " + facultyId, e);
            }
            if (faculty != null) {
                faculty.setEnrollmentIsOver(true);
                boolean flag;
                try {
                    flag = facultyService.updateFaculty(faculty);
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while facultyTimeOutCommand because of updating faculty with id" + facultyId, e);
                }
                if (!flag) {
                    System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
                    LOGGER.log(Level.ERROR, "There is a problem while facultyTimeOutCommand because of updating faculty with id" + facultyId);
                    return CommandParameterConst.EMPTY_STRING;
                }
            }
            ArrayList<Integer> entrantsId;
            boolean flag;
            try {
                entrantsId = entrantService.sortEntrantsByTotalMark(facultyId);
                Integer entry_plan = facultyService.findFacultyById(facultyId).getEntryPlan();
                for (int i = 0; i < entrantsId.size(); i++) {
                    Entrant entrant = entrantService.findEntrantById(entrantsId.get(i));
                    if (i < entry_plan) {
                        entrant.setStatus(Entrant.Status.SUBMITTED);
                    } else {
                        entrant.setStatus(Entrant.Status.CANCELLED);
                    }
                    entrant.setUnavailable(true);
                    flag = entrantService.updateEntrant(entrant);
                    if (!flag) {
                        LOGGER.log(Level.ERROR, "There is a problem while facultyTimeOutCommand because of updating entrant");
                        return CommandParameterConst.EMPTY_STRING;
                    }
                }
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while counting submitted students! ", e);
            }
        }
        return CommandParameterConst.EMPTY_STRING;
    }

    @Override
    public String doPost(HttpServletRequest req, HttpServletResponse res) {
        return null;
    }
}
