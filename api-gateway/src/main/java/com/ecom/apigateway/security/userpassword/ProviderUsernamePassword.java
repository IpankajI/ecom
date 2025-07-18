package com.ecom.apigateway.security.userpassword;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ecom.apigateway.service.UserService;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class ProviderUsernamePassword implements AuthenticationProvider{
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String username=(String)authentication.getPrincipal();
        String password=(String)authentication.getCredentials();
        
        if(username==null || password==null || Boolean.TRUE.equals(!userService.verifyUser(username, password))){
            return null;
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
