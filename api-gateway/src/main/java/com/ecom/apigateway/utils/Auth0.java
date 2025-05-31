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
        tokenRequest.setClientId("zyzmHGAIj0TmCOd6vqHcyKQScLjbnMMT");
        tokenRequest.setClientSecret("e72mF1wmapJd0Gnp-xqBSGKLUzflw7IlHoZlq12jBzoVmooa6Pw_Qk7jQNkLvwoh");
        tokenRequest.setGrantType("http://auth0.com/oauth/grant-type/passwordless/otp");
        tokenRequest.setOtp(otp);
        tokenRequest.setRealm("sms");
        tokenRequest.setScope("openid profile email offline_access");
        tokenRequest.setUsername(phoneNumber);


        TokenResponse tokenResponse=webClient
            .post()
            .uri("https://dev-mg2f3czkzybsk1k2.us.auth0.com/oauth/token")
            .bodyValue(tokenRequest)
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();

        return tokenResponse;
    }

    public Auth0OtpResponse RequestOtp(String phoneNumber){
        Auth0OtpRequest auth0OtpRequest=new Auth0OtpRequest();
        auth0OtpRequest.setClientId("zyzmHGAIj0TmCOd6vqHcyKQScLjbnMMT");
        auth0OtpRequest.setClientSecret("e72mF1wmapJd0Gnp-xqBSGKLUzflw7IlHoZlq12jBzoVmooa6Pw_Qk7jQNkLvwoh");
        auth0OtpRequest.setConnection("sms");
        auth0OtpRequest.setPhoneNumber(phoneNumber);
        auth0OtpRequest.setSend("code");

        Auth0OtpResponse auth0OtpResponse=webClient
            .post()
            .uri("https://dev-mg2f3czkzybsk1k2.us.auth0.com/passwordless/start")
            .bodyValue(auth0OtpRequest)
            .retrieve()
            .bodyToMono(Auth0OtpResponse.class)
            .block();

        return auth0OtpResponse;
    }

}

// {
//     "connection": "sms",
//     "phone_number": "+919454243912",
//     "send": "code",
//     "client_id": "zyzmHGAIj0TmCOd6vqHcyKQScLjbnMMT",
//     "client_secret": "e72mF1wmapJd0Gnp-xqBSGKLUzflw7IlHoZlq12jBzoVmooa6Pw_Qk7jQNkLvwoh"
// }
// {
//     "grant_type": "http://auth0.com/oauth/grant-type/passwordless/otp",
//     "client_id": "zyzmHGAIj0TmCOd6vqHcyKQScLjbnMMT",
//     "client_secret": "e72mF1wmapJd0Gnp-xqBSGKLUzflw7IlHoZlq12jBzoVmooa6Pw_Qk7jQNkLvwoh",
//     "username": "+919454243912",
//     "otp": "129706",
//     "realm": "sms",
//     "audience": "https://api.example.com",
//     "scope": "openid profile email offline_access"
// }

