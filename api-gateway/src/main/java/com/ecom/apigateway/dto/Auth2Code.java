package com.ecom.apigateway.dto;

public record Auth2Code(String codeVerifier, String codeChallenge) {
}
