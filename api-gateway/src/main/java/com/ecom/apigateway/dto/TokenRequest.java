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
public class TokenRequest {
    
    @JsonProperty("grant_type")
    private String grantType;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("username")
    private String username;
    @JsonProperty("otp")
    private String otp;
    @JsonProperty("realm")
    private String realm;
    @JsonProperty("audience")
    private String audience;
    @JsonProperty("scope")
    private String scope;

}

