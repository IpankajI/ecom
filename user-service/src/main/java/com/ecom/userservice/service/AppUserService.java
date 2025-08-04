package com.ecom.userservice.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.userservice.model.AppUser;
import com.ecom.userservice.repository.AppUserRepository;
import com.ecom.userservice.utils.IDGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserService {
    
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IDGenerator idGenerator;

    public AppUser createAppUser(AppUser appUser){
        appUser.setId(idGenerator.next());
        appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    public AppUser getAppUserByPhoneNumber(String phoneNumber){
        return appUserRepository.getByPhoneNumber(phoneNumber);
    }

    public AppUser getAppUserByUsername(String username){
        return appUserRepository.getByName(username);
    }

    public Boolean verifyUsernamePassword(String username, String password){
        AppUser appUser=appUserRepository.getByName(username);
        return bCryptPasswordEncoder.matches(password, appUser.getPassword());
    }

    public AppUser getAppUserByEmail(String email){
        return appUserRepository.getByEmail(email);
    }
}
