package com.ecom.apigateway.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class OAuth2 {
    public static String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifierBytes = new byte[32]; // 32 bytes for a 43-128 char verifier
        secureRandom.nextBytes(codeVerifierBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierBytes);
    }

    public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        try {
            byte[] bytes = codeVerifier.getBytes("US-ASCII");
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new NoSuchAlgorithmException("Error generating code challenge: " + e.getMessage(), e);
        }
    }
}
