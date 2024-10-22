package com.is4tech.invoicemanagement.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    public static String validatePassword(String password) {
        StringBuilder message = new StringBuilder("La contraseña es inválida. Faltan los siguientes requisitos:");
        boolean isValid = true;

        if (password.length() < 8) {
            message.append("al menos 8 caracteres, ");
            isValid = false;
        }

        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Matcher haslowerCase = lowerCasePattern.matcher(password);
        if (!haslowerCase.find()) {
            message.append("al menos 1 letra minúscula, ");
            isValid = false;
        }

        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Matcher hasUpperCase = upperCasePattern.matcher(password);
        if (!hasUpperCase.find()) {
            message.append("al menos 1 letra mayúscula, ");
            isValid = false;
        }

        Pattern digitPattern = Pattern.compile("\\d");
        Matcher hasDigit = digitPattern.matcher(password);
        if (!hasDigit.find()) {
            message.append("al menos 1 número, ");
            isValid = false;
        }

        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        Matcher hasSpecialChar = specialCharPattern.matcher(password);
        if (!hasSpecialChar.find()) {
            message.append("al menos 1 carácter especial, ");
            isValid = false;
        }

        if (isValid) {
            return "La contraseña es valida.";
        }

        return message.substring(0, message.length() - 2) + ".";
    }
}
