package com.ecom.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppUserRequest {
    private String phoneNumber;
    private String name;
    private String password;
}
