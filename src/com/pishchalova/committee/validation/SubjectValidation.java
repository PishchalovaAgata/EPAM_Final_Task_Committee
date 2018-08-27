package com.pishchalova.committee.validation;


public class SubjectValidation {

    public static boolean validateSubjectParameters(String subjectName) {
        return FieldEntityValidation.validateTextCollocation(subjectName);
    }
}
