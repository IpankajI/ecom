package com.ecom.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserResponse {
    private Long id;
    private String phoneNumber;
    private String name;
    private String email;
}
