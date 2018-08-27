package com.pishchalova.committee.command.faculty;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.exception.WrongDateFormattingException;
import com.pishchalova.committee.service.EntrantService;
import com.pishchalova.committee.service.FacultyService;
import com.pishchalova.committee.service.SubjectService;
import com.pishchalova.committee.service.UserService;
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

public class GetFacultiesCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(GetFacultiesCommand.class);
    private static final Double AMOUNT_FACULTIES_ON_ONE_PAGE = 5.0;

    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        FacultyService facultyService = new FacultyService();
        ArrayList<Faculty> faculties;
        UserService userService = new UserService();
        EntrantService entrantService = new EntrantService();
        Integer userId = (Integer) request.getServletContext().getAttribute(request.getSession().getId());

        String body;
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new CommandException("There is a problem while login user because of getting body from request!", e);
        }
        JSONObject jObj = new JSONObject(body);
        Integer numberOfPage = jObj.getInt(CommandParameterConst.PAGE);
        JSONObject filterObject = jObj.getJSONObject(CommandParameterConst.FILTER);
        boolean filterOrder;
        int amountPages;

        if (filterObject.length() == 0) {
            try {
                if (userService.findUserById(userId).getRole() == User.Role.ENTRANT && entrantService.filterEntrantsByUserId(userId) != null) {
                    ArrayList<Integer> subjectsId = entrantService.findSubjectsByEntrantId(entrantService.filterEntrantsByUserId(userId).getEntrantId());
                    LOGGER.log(Level.INFO, "Try to filter faculties for entrant. SubjectsIds:" + subjectsId.toString());
                    faculties = facultyService.filterFacultiesBySubjects((int) ((numberOfPage - 1) * AMOUNT_FACULTIES_ON_ONE_PAGE), false, subjectsId.toArray(new Integer[0]));
                    amountPages = (int) Math.ceil(facultyService.ffff(subjectsId.toArray(new Integer[0])) / AMOUNT_FACULTIES_ON_ONE_PAGE);
                } else {
                    LOGGER.log(Level.INFO, "Try to filter faculties!");
                    faculties = facultyService.filterFacultiesBySubjects((int) ((numberOfPage - 1) * AMOUNT_FACULTIES_ON_ONE_PAGE), false);
                    amountPages = (int) Math.ceil(facultyService.countAmountOfAllFaculties() / AMOUNT_FACULTIES_ON_ONE_PAGE);
                }
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while getting faculties because of getting part of faculties!", e);
            }
        } else {
            filterOrder = filterObject.getBoolean(CommandParameterConst.ORDER);
            JSONArray subjects = filterObject.getJSONArray(CommandParameterConst.SUBJECTS);
            ArrayList<Integer> subjectsId = new ArrayList<>();
            SubjectService subjectService = new SubjectService();
            for (int i = 0; i < subjects.length(); i++) {
                String subjectName = (String) subjects.get(i);
                try {
                    subjectsId.add(subjectService.filterSubjectsByName(subjectName).getId());
                } catch (ServiceException e) {
                    throw new CommandException("There is a problem while getting faculties because of configuration subjects list!", e);
                }
            }
            LOGGER.log(Level.INFO, "Try to filter faculties. SubjectsIds:" + subjectsId.toString() + " order " + filterOrder);
            try {
                faculties = facultyService.filterFacultiesBySubjects((int) ((numberOfPage - 1) * AMOUNT_FACULTIES_ON_ONE_PAGE), filterOrder, subjectsId.toArray(new Integer[0]));
                amountPages = (int) Math.ceil(facultyService.ffff(subjectsId.toArray(new Integer[0])) / AMOUNT_FACULTIES_ON_ONE_PAGE);
                System.out.println(amountPages);
            } catch (ServiceException e) {
                throw new CommandException("There is a problem while getting faculties because of filtering faculties by subjects !", e);
            }
        }

        JSONObject finalObject = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
        JSONArray jsonObjectArrayList = new JSONArray();

        if (faculties != null) {
            for (Faculty faculty : faculties) {
                JSONObject object = new JSONObject(CommandParameterConst.EMPTY_JSON_OBJECT);
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
                        throw new CommandException("There is a problem while getting faculties because of filtering subjects by id!", e);
                    }
                }
                object.put(CommandParameterConst.SUBJECTS, subjectsForFaculty);
                try {
                    object.put(CommandParameterConst.TIME, faculty.getEndDateReceivingAsLong());
                } catch (WrongDateFormattingException e) {
                    throw new CommandException("There is a problem while getting faculties because of getting date for faculties as long!", e);
                }
                jsonObjectArrayList.put(object);
            }
            finalObject.put(CommandParameterConst.TOTAL_PAGES, amountPages);
            finalObject.put(CommandParameterConst.FACULTIES, jsonObjectArrayList);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return finalObject.toString();
        }
        return CommandParameterConst.EMPTY_STRING;
    }
}
