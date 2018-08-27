package com.pishchalova.committee.validation;


public class SubjectValidation {

    public static boolean validateSubjectParameters(String... subjectName){
        return subjectName.length == 3
                && FieldEntityValidation.validateTextCollocation(subjectName);
    }

    public static boolean validateSubjectParameters(String subjectName) {
        return FieldEntityValidation.validateTextCollocation(subjectName);
    }





















   /* private static final String VALID_SUBJECT = "(\\p{Upper}([a-z]*)\\p{Blank})*(\\p{Upper}([a-z]+))";
    private static final Pattern PATTERN_VALID_SUBJECT = Pattern.compile(VALID_SUBJECT);

    public static boolean validateSubject(String login) throws ServiceException {
        Matcher matcher = PATTERN_VALID_SUBJECT.matcher(login);
        if (!matcher.matches()) {
            throw new ServiceException("Subject name is not valid!");
        }
        return matcher.matches();
    }*/
}
