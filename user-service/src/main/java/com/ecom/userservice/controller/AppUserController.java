package com.ecom.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.userservice.dto.CreateAppUserRequest;
import com.ecom.userservice.dto.AppUserResponse;
import com.ecom.userservice.dto.VerifyUsernamePasswordRequest;
import com.ecom.userservice.model.AppUser;
import com.ecom.userservice.service.AppUserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "user apis")
public class AppUserController {
    
    private final AppUserService appUserService;

    @PostMapping()
    public AppUserResponse createAppUser(@RequestBody CreateAppUserRequest request){
        AppUser appUser=AppUser.builder()
            .name(request.getName())
            .password(request.getPassword())
            .phoneNumber(request.getPhoneNumber())
            .build();
        appUserService.createAppUser(appUser);

        return toAppUserResponseFrom(appUser);
    }

    @GetMapping("/{username}")
    public AppUserResponse getAppUserByUsername(@PathVariable("username") String username){
        return toAppUserResponseFrom(appUserService.getAppUserByUsername(username));
    }

    private AppUserResponse toAppUserResponseFrom(AppUser appUser){
        return AppUserResponse.builder()
            .id(appUser.getId())
            .name(appUser.getName())
            .phoneNumber(appUser.getPhoneNumber())
            .build();
    }

    @PostMapping("/verify")
    public Boolean verifyUsernamePassword(@RequestBody VerifyUsernamePasswordRequest request){
        return appUserService.verifyUsernamePassword(request.getName(), request.getPassword());
    }

    @GetMapping("/phone-number/{phoneNumber}")
    public AppUserResponse getAppUserByPhoneNumber(@PathVariable("phoneNumber") String phoneNumber){
        return toAppUserResponseFrom(appUserService.getAppUserByPhoneNumber(phoneNumber));
    }
}
