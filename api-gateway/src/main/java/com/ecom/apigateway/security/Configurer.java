package com.ecom.apigateway.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecom.apigateway.security.apikey.ProviderApiKey;
import com.ecom.apigateway.security.jwt.ProviderJwt;
import com.ecom.apigateway.security.otp.ProviderOtp;
import com.ecom.apigateway.security.userpassword.ProviderUsernamePassword;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class Configurer{
	private final ProviderUsernamePassword authProviderUP;
    private final ProviderApiKey authProviderApiKey;
    private final ProviderJwt authProviderJwt;
    private final ProviderOtp authProviderOtp;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return new ProviderManager(
                Arrays.asList(
                    authProviderUP, 
                    authProviderApiKey, 
                    authProviderJwt,
                    authProviderOtp,
                    new DaoAuthenticationProvider()
                )
            );
    }
}
