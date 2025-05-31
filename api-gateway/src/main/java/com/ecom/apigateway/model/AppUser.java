package com.ecom.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUser{
    
    private Integer id;
    private String phoneNumber;
    private String name;
    private String password;
   
}