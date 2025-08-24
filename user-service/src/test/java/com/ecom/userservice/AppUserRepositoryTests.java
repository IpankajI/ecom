package com.ecom.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ecom.userservice.model.AppUser;
import com.ecom.userservice.repository.AppUserRepository;

@DataJpaTest
class AppUserRepositoryTests {
    
    @Autowired
    private AppUserRepository appUserRepository;
    
    @BeforeEach
    void cleanUp(){
        appUserRepository.deleteAll();
    }

    @Test
    void testGetByPhoneNumber(){

        AppUser appUser=new AppUser(2l, "+919454243912", "pankaj", "password", "email@gamil.com");

        appUserRepository.save(appUser);

        AppUser actualAppUser=appUserRepository.getByPhoneNumber("+919454243912");
        assertNotNull(actualAppUser);
        assertEquals(appUser, actualAppUser);
    }

    @Test
    void testGetByName(){
        AppUser appUser=new AppUser(2l, "+919454243912", "pankaj", "password", "email@gamil.com");
        appUserRepository.save(appUser);
        AppUser actualAppUser=appUserRepository.getByName("pankaj");
        assertNotNull(actualAppUser);
        assertEquals(appUser, actualAppUser);
    }

    @Test
    void testGetByEmail(){
        AppUser appUser=new AppUser(2l, "+919454243912", "pankaj", "password", "email@gamil.com");
        appUserRepository.save(appUser);
        AppUser actualAppUser=appUserRepository.getByEmail("email@gamil.com");
        assertNotNull(actualAppUser);
        assertEquals(appUser, actualAppUser);
    }
}
