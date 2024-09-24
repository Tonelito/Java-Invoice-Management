package com.is4tech.invoicemanagement.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class ResetCodeGenerator {

    private ResetCodeGenerator(){}
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); // Evita caracteres como "+", "/" o "="

    public static String generateResetCode(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes).substring(0, length);
    }
}
