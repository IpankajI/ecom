package com.ecom.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    @InjectMocks
    private AppUserService appUserService;

    AppUser appUser;

    @BeforeEach
    void setUp(){
        appUser=new AppUser(null, "+919454243912", "pankaj", "password", "email@gamil.com");
    }

    @Test
    void testCreateAppUser(){
        when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);
        when(idGenerator.next()).thenReturn(11l);
        when(bCryptPasswordEncoder.encode(any(CharSequence.class))).thenReturn("xxx");

        AppUser createdAppUser=appUserService.createAppUser(appUser);
        assertNotNull(createdAppUser);

        assertEquals(11l, createdAppUser.getId());
    }

}
