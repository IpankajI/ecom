package com.ecom.apigateway.security.jwt;



import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecom.apigateway.appconfig.HttpXHeader;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FilterJwt extends OncePerRequestFilter{

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,  @NonNull HttpServletResponse response,  @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String authToken = request.getHeader(HttpXHeader.AUTHORIZATION);
        if(authToken==null || !authToken.startsWith(HttpXHeader.BEARER)){
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authToken.substring(HttpXHeader.BEARER.length()+1);
		AuthNJwt authRequest = new AuthNJwt(jwt, null);
        authenticationManager.authenticate(authRequest);
        filterChain.doFilter(request, response);
    }
}
