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
public class OtpResponse {
    @JsonProperty("to")
    private String to;
    @JsonProperty("valid")
    private boolean valid;
    @JsonProperty("status")
    private String status;
}
