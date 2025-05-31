package com.ecom.apigateway.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auth0OtpResponse {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("phone_verified")
    private String phoneVerified;
    @JsonProperty("request_language")
    private String requestLanguage;
}
