package com.ecom.apigateway.security.jwt;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


public class AuthNJwt implements Authentication{
    
    private String jwtToken;
    private boolean authenticated;
    private String subject;

    public AuthNJwt(String subject){
        this.subject=subject;
        this.authenticated=true;
    }

    public AuthNJwt(String jwtToken, String subject){
        this.jwtToken=jwtToken;
        this.subject=subject;
        this.authenticated=false;
    }

    @Override
    public String getName() {
        return "apiKey";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    @Override
    public Object getDetails() {
        throw new UnsupportedOperationException("Unimplemented method 'getDetails'");
    }

    @Override
    public Object getPrincipal() {
        return subject;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'setAuthenticated'");
    }
    
}
