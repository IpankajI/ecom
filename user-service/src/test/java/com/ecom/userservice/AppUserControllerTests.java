package com.ecom.userservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecom.userservice.controller.AppUserController;
import com.ecom.userservice.dto.CreateAppUserRequest;
import com.ecom.userservice.dto.VerifyUsernamePasswordRequest;
import com.ecom.userservice.model.AppUser;
import com.ecom.userservice.service.AppUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AppUserController.class)
@ExtendWith(MockitoExtension.class)
class AppUserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppUserService appUserService;

    @Spy
    private AppUser appUser;

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
    void createAppUser() throws Exception{
        appUser.setId(11L);
        when(appUserService.createAppUser(any(AppUser.class))).thenReturn(appUser);

        CreateAppUserRequest createAppUserRequest=new CreateAppUserRequest(phone, name, password, email);

        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createAppUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(11L))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.phoneNumber").value(phone))
            .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void testGetAppUserByUsername() throws Exception{

        appUser.setId(11L);
        when(appUserService.getAppUserByUsername(name)).thenReturn(appUser);
        
        mockMvc.perform(get("/api/users/pankaj"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(11L))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.phoneNumber").value(phone))
            .andExpect(jsonPath("$.email").value(email));
    }
    

    @Test
    void testGetAppUserSearchEmail()throws Exception{
        appUser.setId(11L);
        when(appUserService.getAppUserByEmail(email)).thenReturn(appUser);
        
        mockMvc.perform(get("/api/users/search?email="+email))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(11L))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.phoneNumber").value(phone))
            .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void testGetAppUserSearchPhone()throws Exception{
        appUser.setId(11L);
        when(appUserService.getAppUserByPhoneNumber(phone)).thenReturn(appUser);
        
        mockMvc.perform(get("/api/users/search?phone="+phone))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(11L))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.phoneNumber").value(phone))
            .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void testVerifyUsernamePassword() throws Exception{
        appUser.setId(11L);
        when(appUserService.verifyUsernamePassword(name, password)).thenReturn(true);

        VerifyUsernamePasswordRequest verifyUsernamePasswordRequest=new VerifyUsernamePasswordRequest(name, password);

        mockMvc.perform(post("/api/users/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(verifyUsernamePasswordRequest))
        )
        .andExpect(status().isOk())
        .andExpect(content().string("true"));


        when(appUserService.verifyUsernamePassword(name, password+"wrong")).thenReturn(false);

        verifyUsernamePasswordRequest=new VerifyUsernamePasswordRequest(name, password+"wrong");

        mockMvc.perform(post("/api/users/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(verifyUsernamePasswordRequest))
        )
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
    }

    @Test
    void testGetAppUserByPhoneNumber() throws Exception{
        appUser.setId(11L);
        when(appUserService.getAppUserByPhoneNumber(phone)).thenReturn(appUser);
        
        mockMvc.perform(get("/api/users/phone-number/{phoneNumber}", phone))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(11L))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.phoneNumber").value(phone))
            .andExpect(jsonPath("$.email").value(email));
    }
}
