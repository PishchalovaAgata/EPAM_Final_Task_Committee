package com.pishchalova.committee.validation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldEntityValidation {
    private static final Logger LOGGER = LogManager.getLogger(FieldEntityValidation.class);


    private static final String COLLOCATION_REGEX_EXPRESSION = "(\\p{Upper}(\\p{Lower}*)\\p{Blank})*(\\p{Upper}(\\p{Lower}+))";
    private static final String NUMBER_IN_RANGE_FORTY_AND_HUNDRED = "^([4-9]\\d|100)$";
    private static final String NUMBER_IN_RANGE_TWENTY_AND_HUNDRED = "^([2-9]\\d|100)$";
    private static final String NUMBER_IN_RANGE_TWENTY_AND_THOUSAND = "^([2-9]\\d|[1-9]\\d\\d|1000)$";


    private static final String VALID_PASSWORD = "^(?=.*\\d)(?=.*\\p{Lower})(?=.*\\p{Upper})(?!.*\\s).{6,20}$";
    private static final String VALID_EMAIL = "^[\\w]+([.-][\\w]+)*@(\\w[-A-z0-9]+\\.)+[A-z]{2,4}$";
    private static final String VALID_LOGIN = "^[a-zA-Z][a-zA-Z0-9-_.]{3,20}$";

    private static final String VALID_DATE_FORMAT = "yyyy-MM-dd";

    @SafeVarargs
    private static <T> boolean checkNull(T... values) {
        if (values == null) {
            return true;
        } else {
            for (T value : values) {
                if (value == null) {
                    return true;
                }
            }
            return false;
        }
    }


    static boolean validateTextCollocation(String... values) {
        if (checkNull(values)) {
            LOGGER.log(Level.INFO, "Text Expressions are not filled!");
            return false;
        }
        LOGGER.log(Level.INFO, "Validation for text Collocations: ");
        for (String value : values) {
            Matcher matcher = Pattern.compile(COLLOCATION_REGEX_EXPRESSION).matcher(value);
            LOGGER.log(Level.INFO, " Validation for Phase: " + value + " " + matcher.matches());
            if (!matcher.matches()) {
                LOGGER.log(Level.INFO, "Validation is completed with problems! ");
                return false;
            }
        }
        LOGGER.log(Level.INFO, "Validation is successfully completed! ");
        return true;
    }


    public static boolean validateTextNumberInRangeFortyAndHundred(String... values) {
        if (checkNull(values)) {
            LOGGER.log(Level.INFO, "Number Expressions are not filled!");
            return false;
        }
        LOGGER.log(Level.INFO, "Validation for number(40;100) Collocations: ");
        for (String value : values) {
            Matcher matcher = Pattern.compile(NUMBER_IN_RANGE_FORTY_AND_HUNDRED).matcher(value);
            LOGGER.log(Level.INFO, " Validation for Number(40;100): " + value + " " + matcher.matches());
            System.out.println(matcher + " " + matcher.matches());//todo!!!
            if (!matcher.matches()) {
                LOGGER.log(Level.INFO, "Validation is completed with problems! ");
                return false;
            }
        }
        LOGGER.log(Level.INFO, "Validation is successfully completed! ");
        return true;
    }

    public static boolean validateTextNumberInRangeTwentyAndHundred(String... values) {//todo: for marks
        if (checkNull(values)) {
            LOGGER.log(Level.INFO, "Number Expressions are not filled!");
            return false;
        }
        LOGGER.log(Level.INFO, "Validation for numbers(20;100) Collocations: ");
        for (String value : values) {
            Matcher matcher = Pattern.compile(NUMBER_IN_RANGE_TWENTY_AND_HUNDRED).matcher(value);
            LOGGER.log(Level.INFO, " Validation for Number(20;100): " + value + " " + matcher.matches());

            if (!matcher.matches()) {
                LOGGER.log(Level.INFO, "Validation is completed with problems! ");
                return false;
            }
        }
        LOGGER.log(Level.INFO, "Validation is successfully completed! ");

        return true;
    }

    public static boolean validateTextNumberInRangeTwentyAndThousand(Integer... values){
        if (checkNull(values)) {
            LOGGER.log(Level.INFO, "Number Expressions are not filled!");
            return false;
        }
        LOGGER.log(Level.INFO, "Validation for numbers(20;1000) Collocations: ");

        for (Integer value : values) {
            Matcher matcher = Pattern.compile(NUMBER_IN_RANGE_TWENTY_AND_THOUSAND).matcher(value.toString());
            LOGGER.log(Level.INFO, " Validation for Number(20;1000): " + value + " " + matcher.matches());
            if (!matcher.matches()) {
                LOGGER.log(Level.INFO, "Validation is completed with problems! ");
                return false;
            }
        }
        LOGGER.log(Level.INFO, "Validation is successfully completed! ");
        return true;
    }

    public static boolean isValidDate(String input) {
        try {
            new SimpleDateFormat(VALID_DATE_FORMAT).parse(input);
            LOGGER.log(Level.INFO, "Validation of date is successfully completed! ");
            return true;
        } catch (ParseException e) {
            LOGGER.log(Level.INFO, "Validation of date is completed with problems!");
            return false;
        }
    }

    public static boolean validateLogin(String login) {
        if (checkNull(login)) {
            LOGGER.log(Level.INFO, "Login is not filled!");
            return false;
        }
        LOGGER.log(Level.INFO, "Validation for login Collocations: ");
        Matcher matcher = Pattern.compile(VALID_LOGIN).matcher(login);
        Boolean result = matcher.matches();
        LOGGER.log(Level.INFO, " Validation for Login: " + result);
        if (!result) {
            LOGGER.log(Level.INFO, "Validation is completed with problems! ");
        } else {
            LOGGER.log(Level.INFO, "Validation is successfully completed! ");
        }
        return result;
    }

    public static boolean passwordValidation(String password) {
        if (checkNull(password)) {
            LOGGER.log(Level.INFO, "Password is not filled!");
            return false;
        }
        LOGGER.log(Level.INFO, "Validation for password Collocations: ");
        Matcher matcher = Pattern.compile(VALID_PASSWORD).matcher(password);
        Boolean result = matcher.matches();
        LOGGER.log(Level.INFO, " Validation for Password: " + result);
        if (!result) {
            LOGGER.log(Level.INFO, "Validation is completed with problems! ");
        } else {
            LOGGER.log(Level.INFO, "Validation is successfully completed! ");
        }
        return result;
    }

    public static boolean emailValidation(String email) {
        if (checkNull(email)) {
            LOGGER.log(Level.INFO, "Email is not filled!");
            return false;
        }
        LOGGER.log(Level.INFO, "Validation for email Collocations: ");
        Matcher matcher = Pattern.compile(VALID_EMAIL).matcher(email);
        Boolean result = matcher.matches();
        LOGGER.log(Level.INFO, " Validation for Email: " + result);
        if (!result) {
            LOGGER.log(Level.INFO, "Validation is completed with problems! ");
        } else {
            LOGGER.log(Level.INFO, "Validation is successfully completed! ");
        }
        return result;
    }
}
