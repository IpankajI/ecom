package com.ecom.apigateway.security.userpassword;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;


@Configuration
public class ProviderUsernamePassword implements AuthenticationProvider{

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username=(String)authentication.getPrincipal();
        String password=(String)authentication.getCredentials();
        
        if(username==null || password==null || !username.equals("username1") || !password.equals("password1")){
            throw new BadCredentialsException("username or password not correct");
        }

        AuthNUsernamePassword auth=new AuthNUsernamePassword(username);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthNUsernamePassword.class);
    }
    
}
