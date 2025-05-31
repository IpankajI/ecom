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
public class Auth0OtpRequest {
    @JsonProperty("connection")
    private String connection;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("send")
    private String send;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
}
