package com.ecom.apigateway.security.otp;



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
public class FilterOtp extends OncePerRequestFilter{
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,  @NonNull HttpServletResponse response,  @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
		String phoneNumber = request.getHeader(HttpXHeader.X_PHONE_NUMBER);
		String otp = request.getHeader(HttpXHeader.X_OTP);

        if (phoneNumber==null || phoneNumber.isEmpty() || otp==null || otp.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
		
		AuthNOtp authRequest = new AuthNOtp(phoneNumber, otp);
        authenticationManager.authenticate(authRequest);
        filterChain.doFilter(request, response);
    }
}
