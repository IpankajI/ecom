package com.ecom.apigateway.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecom.apigateway.appconfig.HttpXHeader;
import com.ecom.apigateway.dto.SignUpRequest;
import com.ecom.apigateway.dto.TokenResponse;
import com.ecom.apigateway.dto.UserResponse;
import com.ecom.apigateway.service.OtpService;
import com.ecom.apigateway.service.UserService;
import com.ecom.apigateway.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
public class AuthenticationController {
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final UserService userservice;

    @PostMapping("/login/username")
    public TokenResponse loginUsername(HttpServletRequest request){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=(String)authentication.getPrincipal();
        TokenResponse tokenResponse=new TokenResponse();
        tokenResponse.setAccessToken(jwtUtil.generateToken(username));
        return tokenResponse;
    }

    @PostMapping("/otp/request")
    public String requestOtp(@RequestHeader(HttpXHeader.X_PHONE_NUMBER) String phoneNumber){
        return otpService.requestOtp(phoneNumber);
    }

    @PostMapping("/login/otp")
    public TokenResponse loginOTP(@RequestHeader(HttpXHeader.X_PHONE_NUMBER) String phoneNumber,
            @RequestHeader(HttpXHeader.X_OTP) String otp){
        String username=userservice.getUserByPhoneNumber(phoneNumber).getName();
        TokenResponse tokenResponse=new TokenResponse();
        tokenResponse.setAccessToken(jwtUtil.generateToken(username));
        return tokenResponse;
    }

    @PostMapping("/sign-up")
    public UserResponse signup(@RequestBody SignUpRequest signUpRequest){
        return userservice.createUser(signUpRequest);
    }

    @GetMapping("/user")
    public UserResponse user(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return userservice.getUser((String)authentication.getPrincipal());
    }

}
