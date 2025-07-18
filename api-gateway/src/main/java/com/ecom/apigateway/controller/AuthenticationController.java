package com.ecom.apigateway.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.apigateway.appconfig.HttpXHeader;
import com.ecom.apigateway.dto.TokenResponse;
import com.ecom.apigateway.service.OtpService;
import com.ecom.apigateway.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public TokenResponse login(HttpServletRequest request){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=(String)authentication.getPrincipal();
        TokenResponse tokenResponse=new TokenResponse();
        tokenResponse.setAccessToken(jwtUtil.generateToken(username));
        return tokenResponse;
    }

    @PostMapping("/otp")
    public String requestOtp(HttpServletRequest request){
        String phoneNumber = request.getHeader(HttpXHeader.X_PHONE_NUMBER);
        return otpService.requestOtp(phoneNumber);
    }

}
