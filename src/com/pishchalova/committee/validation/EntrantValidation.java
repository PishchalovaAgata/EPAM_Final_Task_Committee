package com.pishchalova.committee.validation;

import java.util.ArrayList;

public class EntrantValidation {
    public static boolean validateEntrantParameters(String firstName, String surName, String certificate,
                                                    ArrayList<Integer> markValues) {
        return markValues.size() == 3
                && FieldEntityValidation.validateTextNumberInRangeFortyAndHundred(certificate)
                && FieldEntityValidation.validateTextNumberInRangeTwentyAndThousand(markValues.toArray(new Integer[0]))
                && FieldEntityValidation.validateTextCollocation(firstName, surName);
    }
}
