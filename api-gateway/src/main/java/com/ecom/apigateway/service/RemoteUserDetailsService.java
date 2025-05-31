package com.ecom.apigateway.service;

import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.model.AppUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RemoteUserDetailsService {
    
    private final WebClient webClient;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser=webClient.get().uri("http://localhost:30004/api/users/"+username).retrieve().bodyToMono(AppUser.class).block();

        if(appUser==null){
            throw new UsernameNotFoundException("user not found with user name: "+username);
        }
        System.out.println(".........user found: "+appUser.getName()+"@"+appUser.getPhoneNumber());


        return appUser;
    }


}

