package com.ecom.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GitHubEmailResponse {
    @JsonProperty("email")
    private String email;
    @JsonProperty("primary")
    private Boolean primary;
    @JsonProperty("verified")
    private Boolean verified;
}
