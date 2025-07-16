package com.ecom.apigateway.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecom.apigateway.security.apikey.FilterApiKey;
import com.ecom.apigateway.security.jwt.FilterJwt;
import com.ecom.apigateway.security.otp.FilterOtp;
import com.ecom.apigateway.security.userpassword.FilterUsernamePassword;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig{
    private final FilterOtp authFilterOtp;
    private final FilterJwt authFilterJwt;
    private final FilterUsernamePassword authFilterUP;
    private final FilterApiKey authFilterApiKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationConfiguration authenticationConfiguration) throws Exception{

        httpSecurity
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                request -> request
                            .requestMatchers(HttpMethod.POST, "/otp/**").permitAll() 
                            .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll() 
                            .requestMatchers(HttpMethod.GET, "/api/docs/**").permitAll()            
                            .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()            
                            // .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                            // .requestMatchers(HttpMethod.POST,"/verify/**" ).permitAll()
                            .anyRequest().authenticated()
            )
            .csrf(Customizer.withDefaults())
            .addFilterBefore(authFilterOtp, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(authFilterJwt, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(authFilterUP, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(authFilterApiKey, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
