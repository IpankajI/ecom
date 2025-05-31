package com.ecom.apigateway.security.apikey;



import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecom.apigateway.appconfig.HttpXHeader;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;


@Configuration
@AllArgsConstructor
public class FilterApiKey extends OncePerRequestFilter{

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
		String apiKey = request.getHeader(HttpXHeader.xApiKey);
		String apiSecret = request.getHeader(HttpXHeader.xApiSecret);

        if (apiKey==null || apiKey.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
		
		AuthNApiKey authRequest = new AuthNApiKey(apiKey, apiSecret);
        authenticationManager.authenticate(authRequest);
        filterChain.doFilter(request, response);
    }
}
