package com.pishchalova.committee.validation;

public class UserValidation {
    public static boolean validateUserParameters(String login, String password, String email){
        return FieldEntityValidation.validateLogin(login)
                && FieldEntityValidation.passwordValidation(password)
                && FieldEntityValidation.emailValidation(email);
    }




















}