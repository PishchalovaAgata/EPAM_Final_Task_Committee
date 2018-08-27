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

























    /*private static final String VALID_ENTRANT_FIRST_OR_SECOND_NAME = "(\\p{Upper}([a-z]*)\\p{Blank})*(\\p{Upper}([a-z]+))";
    private static final Pattern PATTERN_VALID_ENTRANT_FIRST_OR_SECOND_NAME = Pattern.compile(VALID_ENTRANT_FIRST_OR_SECOND_NAME);

    public static boolean validateFirstOrSecondName(String firstOrSecondName) throws ServiceException {
        Matcher matcher = PATTERN_VALID_ENTRANT_FIRST_OR_SECOND_NAME.matcher(firstOrSecondName);
        if (!matcher.matches()) {
            throw new ServiceException("Entrant First Or Second Name is not valid!");
        }
        return matcher.matches();
    }

    public static boolean validateFacultyCertificate(String entrantCertificate) throws ServiceException {
        int temp;
        try {
            temp = Integer.parseInt(entrantCertificate);
        } catch (NumberFormatException exception) {
            throw new ServiceException("Wrong data format!!!", exception);
        }
        if (temp <= 0 || temp >= 100) {
            throw new ServiceException("Certificate of entrant is not valid!");
        }
        return true;//todo: bred!
    }

    public static boolean validateMarksForEntrant(ArrayList<String> stringMarks) throws ServiceException {
        if (stringMarks.size() != 3) {
            throw new ServiceException("Amount of Marks is not valid!");
        }
        for (String stringMark : stringMarks) {
            int temp;
            try {
                temp = Integer.parseInt(stringMark);
            } catch (NumberFormatException exception) {
                throw new ServiceException("Wrong data format!!!", exception);
            }
            if (temp <= 0 || temp >= 100) {
                throw new ServiceException("Mark of entrant is not valid!");
            }
        }
        return true;
    }*/
}
