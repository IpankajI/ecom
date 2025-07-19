package com.ecom.apigateway.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.apigateway.appconfig.AppConfig;
import com.ecom.apigateway.dto.TokenResponse;
import com.ecom.apigateway.dto.UserResponse;
import com.ecom.apigateway.utils.Auth0;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Hidden
public class Controller {
    
    private AuthenticationManager authenticationManager;
    private Auth0 auth0;
    private AppConfig appConfig;

    @GetMapping("/home")
    public String home(){
        return "welcome! "+appConfig.otpClient;
    }

    @GetMapping("/csrf")
    public CsrfToken cookie(HttpServletRequest httpServletRequest){
        return (CsrfToken)httpServletRequest.getAttribute("_csrf");
    }

    @PostMapping("/")
    public String post(){
        return "xx";
    }

    @PostMapping("/verify/{otp}/{usr}")
    public TokenResponse verify(@RequestBody UserResponse userResponse, @PathVariable("otp") String otp, @PathVariable("usr") String usr) throws JsonProcessingException{

        Authentication authentication= authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userResponse.getPhoneNumber(), "xxxx")
            );

        if(authentication.isAuthenticated()){
            return auth0.getToken(usr, otp);
        }
        
        return new TokenResponse();
    }

}
