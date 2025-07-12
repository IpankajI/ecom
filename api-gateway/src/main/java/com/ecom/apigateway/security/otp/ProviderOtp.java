package com.ecom.apigateway.security.otp;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ecom.apigateway.appconfig.AppConfig;
import com.ecom.apigateway.dto.TokenResponse;
import com.ecom.apigateway.utils.Auth0;
import com.ecom.apigateway.utils.Twilio;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class ProviderOtp implements AuthenticationProvider{

    private final AppConfig appConfig;
    private final Auth0 auth0;
    private final Twilio twilio;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String phoneNumber=(String)authentication.getPrincipal();
        String otp=(String)authentication.getCredentials();
        if(phoneNumber==null || otp==null){
            throw new BadCredentialsException("username or password not correct");
        }
        AuthNOtp auth;
        if(appConfig.otpClient.equals(AppConfig.OTP_CLIENT_AUTH0)){
            TokenResponse tokenResponse=null;
            try {
                tokenResponse = auth0.getToken(phoneNumber, otp);
            } catch (JsonProcessingException e) {
                throw new BadCredentialsException("invalid otp");
            }
            if(tokenResponse.getAccessToken().isEmpty()){
                 throw new BadCredentialsException("username or password not correct");
            }
            auth=new AuthNOtp(phoneNumber);
            auth.setTokenResponse(tokenResponse);

        }
        else{
            if(!twilio.validateOtp(phoneNumber, otp)){
                throw new BadCredentialsException("invalid otp");
            }
            auth=new AuthNOtp(phoneNumber);
            
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthNOtp.class);
    }
    
}
