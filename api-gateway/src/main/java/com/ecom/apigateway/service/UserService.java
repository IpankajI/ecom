package com.ecom.apigateway.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.model.AppUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    final private WebClient webClient;

    public AppUser getUser(String username){
        AppUser appUser=webClient.get().uri("http://localhost:30004/api/users/"+username)
                            .retrieve()
                            .bodyToMono(AppUser.class)
                            .block();
        return appUser;
    }

}