package com.ecom.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ecom.userservice.model.AppUser;
import com.ecom.userservice.repository.AppUserRepository;
import com.ecom.userservice.service.AppUserService;
import com.ecom.userservice.utils.IDGenerator;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTests {

    @Mock
    private AppUserRepository appUserRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private IDGenerator idGenerator;
    @InjectMocks
    private AppUserService appUserService;

    AppUser appUser;

    private final String phone="+919454243912";
    private final String name="pankaj";
    private final String password="password";
    private final String encodedPass="$2a$14$D0ESj9dZ/jlzMRBkFSg9guUrhKuY14s7vonVkF0rBZEletStXYFEu";
    private final String email="email@gamil.com";


    @BeforeEach
    void setUp(){
        bCryptPasswordEncoder=new BCryptPasswordEncoder(14);
        appUser=new AppUser(null, phone, name, encodedPass, email);
    }

    @Test
    void testCreateAppUser(){
        when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);
        when(idGenerator.next()).thenReturn(11l);
        AppUser createdAppUser=appUserService.createAppUser(appUser);
        assertNotNull(createdAppUser);
        assertEquals(11l, createdAppUser.getId());
    }

    @Test
    void testGetAppUserByPhoneNumber(){
        when(appUserRepository.getByPhoneNumber(phone)).thenReturn(appUser);
        AppUser foundAppUser=appUserService.getAppUserByPhoneNumber(phone);
        assertNotNull(foundAppUser);
        assertEquals(appUser, foundAppUser);
        assertEquals(appUser.getName(), foundAppUser.getName());
    }

    @Test
    void testGetAppUserByUsername(){
        when(appUserRepository.getByName(name)).thenReturn(appUser);
        AppUser foundAppUser=appUserService.getAppUserByUsername(name);
        assertNotNull(foundAppUser);
        assertEquals(appUser, foundAppUser);
        assertEquals(appUser.getPhoneNumber(), foundAppUser.getPhoneNumber());
    }

    @Test
    void testVerifyUsernamePassword(){
        when(appUserRepository.getByName(name)).thenReturn(appUser);
        Boolean verified=appUserService.verifyUsernamePassword(name, password);
        assertTrue(verified);

        verified=appUserService.verifyUsernamePassword(name, password+"wrong");
        assertFalse(verified);
    }

    @Test
    void testGetAppUserByEmail(){
        when(appUserRepository.getByEmail(email)).thenReturn(appUser);
        AppUser foundAppUser=appUserService.getAppUserByEmail(email);
        assertNotNull(foundAppUser);
        assertEquals(appUser, foundAppUser);
        assertEquals(appUser.getPhoneNumber(), foundAppUser.getPhoneNumber());
    }
}
