package com.ecom.apigateway.security.apikey;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;


@Configuration
public class ProviderApiKey implements AuthenticationProvider{

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String apiKey=(String)authentication.getPrincipal();
        String apiSecret=(String)authentication.getCredentials();
        
        if(apiKey==null || apiSecret==null || !apiKey.equals("apikey1") || !apiSecret.equals("apisecret1")){
            return null;
        }

        Authentication auth=new AuthNApiKey(apiKey);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(Authentication.class);
    }
    
}
