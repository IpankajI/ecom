package com.ecom.apigateway.security.userpassword;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthNUsernamePassword implements Authentication{

    private String username;
    private String password;
    private boolean authenticated;

    public AuthNUsernamePassword(String username){
        this.username=username;
        this.authenticated=true;
    }

    public AuthNUsernamePassword(String username, String password){
        this.username=username;
        this.password=password;
    }

    @Override
    public String getName() {
        return "username";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getDetails() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDetails'");
    }

    @Override
    public Object getPrincipal() {
        return username;
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
