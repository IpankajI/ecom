package com.ecom.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.userservice.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer>{
    AppUser getByPhoneNumber(String phoneNumber);
}
