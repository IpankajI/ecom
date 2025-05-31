package com.ecom.apigateway.security.userpassword;



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
public class FilterUsernamePassword extends OncePerRequestFilter{

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
		String username = request.getHeader(HttpXHeader.xUsername);
		String password = request.getHeader(HttpXHeader.xPassword);

        if (username==null || username.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
		
		AuthNUsernamePassword authRequest = new AuthNUsernamePassword(username, password);
        authenticationManager.authenticate(authRequest);
        filterChain.doFilter(request, response);
    }
}
