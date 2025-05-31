package com.ecom.apigateway.security.otp;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.ecom.apigateway.dto.TokenResponse;

public class AuthNOtp implements Authentication{

    private String phoneNumber;
    private String otp;
    private boolean authenticated;
    private TokenResponse tokenResponse;

    public AuthNOtp(String phoneNumber){
        this.phoneNumber=phoneNumber;
        this.authenticated=true;
    }

    public AuthNOtp(String phoneNumber, String otp){
        this.phoneNumber=phoneNumber;
        this.otp=otp;
    }

    @Override
    public String getName() {
        return "phone";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return otp;
    }

    @Override
    public Object getDetails() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDetails'");
    }

    @Override
    public Object getPrincipal() {
        return phoneNumber;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAuthenticated'");
    }

    public void setTokenResponse(TokenResponse tokenResponse){
        this.tokenResponse=tokenResponse;
    }

    public TokenResponse getTokenResponse(){
        return this.tokenResponse;
    }
}
