package com.ecom.apigateway.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    public String requestOtp(String phoneNumber){
        switch (appConfig.otpClient) {
            case "auth0":
                Auth0OtpResponse auth0OtpResponse = auth0.requestOtp(phoneNumber);
                if(auth0OtpResponse.getPhoneNumber().equals(phoneNumber)){
                    return "success";
                }
                break;
            case "xx":
                break;
            default:
                return twilio.requestOTP(phoneNumber);
        }

        return "failed";
    }

    public TokenResponse requestToken(){
        TokenResponse tokenResponse=new TokenResponse();
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();

        switch (appConfig.otpClient) {
            case AppConfig.OTP_CLIENT_AUTH0:
                AuthNOtp authNOtp=(AuthNOtp)authentication;
                tokenResponse=authNOtp.getTokenResponse();
                break;
            case "xx":
                break;
            default:
                String subject=(String)authentication.getPrincipal();
                tokenResponse.setAccessToken(jwtUtil.generateToken(subject));
                return tokenResponse;
        }


        return tokenResponse;
    }
}
