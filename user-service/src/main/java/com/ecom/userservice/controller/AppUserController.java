package com.ecom.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.userservice.model.AppUser;
import com.ecom.userservice.service.AppUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {
    
    private final AppUserService appUserService;

    @PostMapping()
    public void createAppUser(@RequestBody AppUser appUser){
        appUserService.createAppUser(appUser);
    }


    // @GetMapping("/{phone_number}")
    // public AppUser getAppUserByPhoneNumber(@PathVariable("phone_number") String phoneNumber){
    //     return appUserService.getAppUserByPhoneNumber(phoneNumber);
    // }

    @GetMapping("/{username}")
    public AppUser getAppUserByUsername(@PathVariable("username") String username){
        return appUserService.getAppUserByUsername(username);
    }

}
