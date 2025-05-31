package com.ecom.apigateway.security.apikey;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthNApiKey implements Authentication{

    private String apiKey;
    private String apiSecret;
    private boolean authenticated;

    public AuthNApiKey(String apiKey){
        this.apiKey=apiKey;
        this.authenticated=true;
    }

    public AuthNApiKey(String apiKey, String apiSecret){
        this.apiKey=apiKey;
        this.apiSecret=apiSecret;
        this.authenticated=false;
    }

    @Override
    public String getName() {
        return "ApiKey";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return apiSecret;
    }

    @Override
    public Object getDetails() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDetails'");
    }

    @Override
    public Object getPrincipal() {
         return apiKey;
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
    
}
