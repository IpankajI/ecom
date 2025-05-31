package com.ecom.apigateway.security.userpassword;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecom.apigateway.model.AppUser;
import com.ecom.apigateway.service.UserService;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class ProviderUsernamePassword implements AuthenticationProvider{

    final private UserService userService;
    final private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String username=(String)authentication.getPrincipal();
        String password=(String)authentication.getCredentials();
        
        if(username==null || password==null){
            return null;
        }

        AppUser appUser=userService.getUser(username);
        if (appUser==null || !passwordEncoder.matches(password, appUser.getPassword())) {
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
