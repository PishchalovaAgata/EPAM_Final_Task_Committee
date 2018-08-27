package com.pishchalova.committee.validation;

import java.util.ArrayList;
import java.util.HashSet;

public class FacultyValidation {

    public static boolean validateFacultyParameters(String facultyName,
                                                    String facultyEntryPlan,
                                                    ArrayList<String> nameSubjects) {
        return nameSubjects.size() == 3
                && new HashSet<>(nameSubjects).size() == 3
                && FieldEntityValidation.validateTextCollocation(nameSubjects.toArray(new String[0]))
                && FieldEntityValidation.validateTextCollocation(facultyName)
                && FieldEntityValidation.validateTextNumberInRangeTwentyAndThousand(Integer.parseInt(facultyEntryPlan));
    }


}
