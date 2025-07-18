package com.ecom.apigateway.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.dto.UserVerifyRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;

    public Boolean verifyUser(String username, String password){

        UserVerifyRequest userVerifyRequest=UserVerifyRequest.builder()
                                                .name(username)
                                                .password(password)
                                                .build();
        System.out.println("......sending req");
        return webClient.post()
                            .uri("http://user-service:30004/api/users/verify")
                            .bodyValue(userVerifyRequest)
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();
    }
}