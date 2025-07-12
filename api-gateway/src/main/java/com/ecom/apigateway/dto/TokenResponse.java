package com.ecom.apigateway.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class TokenResponse implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;    
    @JsonProperty("refresh_token")
    private String refreshToken;    
    @JsonProperty("id_token")
    private String idToken;    
    @JsonProperty("expires_in")
    private Long expiresIn;    
    @JsonProperty("token_type")
    private String tokenType;    
}

