package com.ecom.userservice.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.userservice.model.AppUser;
import com.ecom.userservice.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserService {
    
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void createAppUser(AppUser appUser){
        appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
        appUserRepository.save(appUser);
    }

    public AppUser getAppUserByPhoneNumber(String phoneNumber){
        return appUserRepository.getByPhoneNumber(phoneNumber);
    }

    public AppUser getAppUserByUsername(String username){
        return appUserRepository.getByName(username);
    }

}
