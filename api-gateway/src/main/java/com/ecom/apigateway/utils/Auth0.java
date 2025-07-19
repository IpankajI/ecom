package com.ecom.apigateway.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.dto.Auth0OtpRequest;
import com.ecom.apigateway.dto.Auth0OtpResponse;
import com.ecom.apigateway.dto.TokenRequest;
import com.ecom.apigateway.dto.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Auth0 {
    
    private final WebClient webClient;

    public TokenResponse getToken(String phoneNumber, String otp) throws JsonProcessingException{

        TokenRequest tokenRequest=new TokenRequest();
        tokenRequest.setAudience("https://api.example.com");
        tokenRequest.setClientId(System.getenv("AUTH0_CLIENT_ID"));
        tokenRequest.setClientSecret(System.getenv("AUTH0_CLIENT_SECRET"));
        tokenRequest.setGrantType("http://auth0.com/oauth/grant-type/passwordless/otp");
        tokenRequest.setOtp(otp);
        tokenRequest.setRealm("sms");
        tokenRequest.setScope("openid profile email offline_access");
        tokenRequest.setUsername(phoneNumber);


        return webClient
            .post()
            .uri("https://dev-mg2f3czkzybsk1k2.us.auth0.com/oauth/token")
            .bodyValue(tokenRequest)
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();
    }

    public Auth0OtpResponse requestOtp(String phoneNumber){
        Auth0OtpRequest auth0OtpRequest=new Auth0OtpRequest();
        auth0OtpRequest.setClientId(System.getenv("AUTH0_CLIENT_ID"));
        auth0OtpRequest.setClientSecret(System.getenv("AUTH0_CLIENT_SECRET"));
        auth0OtpRequest.setConnection("sms");
        auth0OtpRequest.setPhoneNumber(phoneNumber);
        auth0OtpRequest.setSend("code");

        return webClient
            .post()
            .uri("https://dev-mg2f3czkzybsk1k2.us.auth0.com/passwordless/start")
            .bodyValue(auth0OtpRequest)
            .retrieve()
            .bodyToMono(Auth0OtpResponse.class)
            .block();
    }

}
