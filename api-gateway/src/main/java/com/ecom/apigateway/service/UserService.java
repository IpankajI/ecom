package com.ecom.apigateway.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.dto.UserVerifyRequest;
import com.ecom.apigateway.dto.SignUpRequest;
import com.ecom.apigateway.dto.UserResponse;

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
        return webClient.post()
                            .uri("http://user-service:30004/api/users/verify")
                            .bodyValue(userVerifyRequest)
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();
    }
    public UserResponse getUser(String username){
        return webClient.get().uri("http://user-service:30004/api/users/"+username)
                            .retrieve()
                            .bodyToMono(UserResponse.class)
                            .block();
    }

    public UserResponse createUser(SignUpRequest signUpRequest){
        return webClient.post()
                            .uri("http://user-service:30004/api/users")
                            .bodyValue(signUpRequest)
                            .retrieve()
                            .bodyToMono(UserResponse.class)
                            .block();
    }

    public UserResponse getUserByPhoneNumber(String phoneNumber){
        return webClient.get().uri("http://user-service:30004/api/users/phone-number/"+phoneNumber)
                            .retrieve()
                            .bodyToMono(UserResponse.class)
                            .block();
    }
}