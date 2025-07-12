package com.ecom.apigateway.security.userpassword;



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
public class FilterUsernamePassword extends OncePerRequestFilter{
    private final AuthenticationManager authenticationManager;
    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,  @NonNull HttpServletResponse response,  @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
		String username = request.getHeader(HttpXHeader.X_USERNAME);
		String password = request.getHeader(HttpXHeader.X_PASSWORD);

        if (username==null || username.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
		
		AuthNUsernamePassword authRequest = new AuthNUsernamePassword(username, password);
        authenticationManager.authenticate(authRequest);
        filterChain.doFilter(request, response);
    }
}
