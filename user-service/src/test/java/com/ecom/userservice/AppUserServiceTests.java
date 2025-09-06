package com.ecom.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private IDGenerator idGenerator;
    @Captor
    private ArgumentCaptor<Long> nextIdCapture;

    @Spy
    private AppUser appUser;

    @InjectMocks
    private AppUserService appUserService;

    private final String phone="+919454243912";
    private final String name="pankaj";
    private final String password="password";
    private final String encodedPass="$2a$14$D0ESj9dZ/jlzMRBkFSg9guUrhKuY14s7vonVkF0rBZEletStXYFEu";
    private final String email="email@gamil.com";


    @BeforeEach
    void setUp(){
        appUser.setPhoneNumber(phone);
        appUser.setEmail(email);
        appUser.setName(name);
        appUser.setPassword(encodedPass);
    }

    @Test
    void testCreateAppUser(){
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        AppUser createdAppUser=appUserService.createAppUser(appUser);
        verify(appUser).setId(nextIdCapture.capture());
        long nextId=nextIdCapture.getValue();
        assertEquals(nextId, createdAppUser.getId());
        assertNotNull(createdAppUser);
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
        when(bCryptPasswordEncoder.matches(password, encodedPass)).thenReturn(true);
        when(bCryptPasswordEncoder.matches("wrong"+password, encodedPass)).thenReturn(false);
        Boolean verified=appUserService.verifyUsernamePassword(name, password);
        assertTrue(verified);

        verified=appUserService.verifyUsernamePassword(name, "wrong"+password);
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
