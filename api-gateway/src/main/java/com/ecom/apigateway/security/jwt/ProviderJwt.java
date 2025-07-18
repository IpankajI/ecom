package com.ecom.apigateway.security.jwt;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ecom.apigateway.utils.JwtUtil;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class ProviderJwt implements AuthenticationProvider{

    private final JwtUtil jwtUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String jwt=(String)authentication.getCredentials();
        if(jwt==null){
            return null;
        }
        String subject=null;
        try {
            if(!jwtUtil.isTokenValid(jwt)){
                return null;
            }
            subject = jwtUtil.extractUserName(jwt);
        } catch (JwtException|IllegalArgumentException|NoSuchAlgorithmException|InvalidKeySpecException e ) {
            return null;
        }

        AuthNJwt auth=new AuthNJwt(subject);
       
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthNJwt.class);
    }
    
}
