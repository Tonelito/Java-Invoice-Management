package com.is4tech.invoicemanagement.utils;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String UPPERCASELETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASELETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";
    private static final String ALL_CHARACTERS = UPPERCASELETTERS + LOWERCASELETTERS + NUMBERS + SPECIAL_CHARACTERS;

    private static final int MIN_PASSWORD_LENGTH = 8;

    public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(UPPERCASELETTERS.charAt(random.nextInt(UPPERCASELETTERS.length())));
        password.append(LOWERCASELETTERS.charAt(random.nextInt(LOWERCASELETTERS.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        int remainingLength = MIN_PASSWORD_LENGTH - password.length();

        for (int i = 0; i < remainingLength; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        return mixCharacters(password.toString());
    }

    private static String mixCharacters(String password) {
        char[] characters = password.toCharArray();

        for (int i = 0; i < characters.length; i++) {
            int randomIndex = i + new SecureRandom().nextInt(characters.length - i);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
