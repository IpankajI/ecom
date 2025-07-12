package com.ecom.apigateway.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.model.AppUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;
    public AppUser getUser(String username){
        return webClient.get().uri("http://user-service:30004/api/users/"+username)
                            .retrieve()
                            .bodyToMono(AppUser.class)
                            .block();
    }

}