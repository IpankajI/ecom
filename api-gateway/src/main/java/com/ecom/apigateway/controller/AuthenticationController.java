package com.ecom.apigateway.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.apigateway.appconfig.HttpXHeader;
import com.ecom.apigateway.dto.TokenResponse;
import com.ecom.apigateway.service.OtpService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final OtpService otpService;

    @PostMapping("/login")
    public TokenResponse login(HttpServletRequest request){
        String phoneNumber = request.getHeader(HttpXHeader.xPhoneNumber);
        String otp = request.getHeader(HttpXHeader.xOtp);
        TokenResponse tokenResponse=otpService.requestToken(phoneNumber, otp);
        return tokenResponse;
    }

    @PostMapping("/otp")
    public String requestOtp(HttpServletRequest request){
        String phoneNumber = request.getHeader(HttpXHeader.xPhoneNumber);
        String response=otpService.RequestOtp(phoneNumber);
        return response;
    }

}
