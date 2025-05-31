package com.ecom.apigateway.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.appconfig.AppConfig;
import com.ecom.apigateway.dto.Auth0OtpResponse;
import com.ecom.apigateway.dto.TokenResponse;
import com.ecom.apigateway.security.otp.AuthNOtp;
import com.ecom.apigateway.utils.Auth0;
import com.ecom.apigateway.utils.JwtUtil;
import com.ecom.apigateway.utils.Twilio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {
    
    private final JwtUtil jwtUtil;
    private final Auth0 auth0;
    private final AppConfig appConfig;
    private final Twilio twilio;

    public String RequestOtp(String phoneNumber){
        switch (appConfig.otpClient) {
            case "auth0":
                Auth0OtpResponse auth0OtpResponse = auth0.RequestOtp(phoneNumber);
                if(auth0OtpResponse.getPhoneNumber().equals(phoneNumber)){
                    return "success";
                }
                break;
        
            default:
                return twilio.requestOTP(phoneNumber);
        }

        return "failed";
    }

    public TokenResponse requestToken(String phoneNumber, String otp){
        TokenResponse tokenResponse=new TokenResponse();
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();

        switch (appConfig.otpClient) {
            case AppConfig.otpClientAuth0:
                AuthNOtp authNOtp=(AuthNOtp)authentication;
                tokenResponse=authNOtp.getTokenResponse();
                System.out.println("....... sv call: "+AppConfig.otpClientAuth0);
                break;
            default:
                String subject=(String)authentication.getPrincipal();
                try {
                    tokenResponse.setAccessToken(jwtUtil.generateToken(subject));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    return tokenResponse;
                }
                System.out.println("....... sv call: "+"NA");
                break;
        }


        return tokenResponse;
    }
}
