package com.ecom.apigateway.appconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

	@Value("${spring.appconfig.otp}")
	public String otpClient;
	public static final String OTP_CLIENT_AUTH0="auth0";
	@Bean
	public WebClient webClient(){
		return WebClient.builder().build();
	}
	@Bean
    public PasswordEncoder bCrypt(){
        return new BCryptPasswordEncoder();
    }

	@Bean
	public JwtUtil jwtUtil(){
		return new JwtUtil(System.getenv("PRIVATE_KEY"), System.getenv("PUBLIC_KEY"));
	}
}
