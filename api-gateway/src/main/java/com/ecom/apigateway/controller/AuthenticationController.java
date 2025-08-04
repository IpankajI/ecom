package com.ecom.apigateway.controller;


import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.ecom.apigateway.appconfig.AppConfig;
import com.ecom.apigateway.appconfig.HttpXHeader;
import com.ecom.apigateway.dto.GitHubAuth2Response;
import com.ecom.apigateway.dto.GitHubEmailResponse;
import com.ecom.apigateway.dto.SignUpRequest;
import com.ecom.apigateway.dto.TokenResponse;
import com.ecom.apigateway.dto.UserResponse;
import com.ecom.apigateway.service.OtpService;
import com.ecom.apigateway.service.UserService;
import com.ecom.apigateway.utils.JwtUtil;
import com.ecom.apigateway.utils.OAuth2;
import com.ecom.apigateway.utils.Redis;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
@Tag(name = "auth apis")
public class AuthenticationController {
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final UserService userservice;
    private final WebClient webClient;
    private final AppConfig appConfig;
    private final Redis redis;

    /*
     * password is not used in this method, however it's required for swagger-ui to show it in form
     */
    @PostMapping("/login/username")
    public TokenResponse loginUsername(@RequestHeader(HttpXHeader.X_USERNAME) String username,
                @RequestHeader(HttpXHeader.X_PASSWORD) String password){
        TokenResponse tokenResponse=new TokenResponse();
        tokenResponse.setAccessToken(jwtUtil.generateToken(username));
        return tokenResponse;
    }

    @PostMapping("/otp/request")
    public String requestOtp(@RequestHeader(HttpXHeader.X_PHONE_NUMBER) String phoneNumber){
        return otpService.requestOtp(phoneNumber);
    }

    /*
     * otp is not used in this method, however it's required for swagger-ui to show it in form
     */
    @PostMapping("/login/otp")
    public TokenResponse loginOTP(@RequestHeader(HttpXHeader.X_PHONE_NUMBER) String phoneNumber,
            @RequestHeader(HttpXHeader.X_OTP) String otp){
        String username=userservice.getUserByPhoneNumber(phoneNumber).getName();
        TokenResponse tokenResponse=new TokenResponse();
        tokenResponse.setAccessToken(jwtUtil.generateToken(username));
        return tokenResponse;
    }

    @PostMapping("/sign-up")
    public UserResponse signup(@RequestBody SignUpRequest signUpRequest){
        return userservice.createUser(signUpRequest);
    }

    @GetMapping("/user")
    public UserResponse user(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return userservice.getUser((String)authentication.getPrincipal());
    }

    
    @GetMapping("/login/social/github")
    public ResponseEntity<Void> loginGithub(){

        String state=UUID.randomUUID().toString();
        String codeVerifier=OAuth2.generateCodeVerifier();
        String codeChallenge=null;
        try{
            codeChallenge=OAuth2.generateCodeChallenge(codeVerifier);
        }
        catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e.getMessage());
        }

        redis.set(state, codeVerifier, codeChallenge);


        String uri=UriComponentsBuilder.fromUriString("https://github.com/login/oauth/authorize")
        .queryParam("code_challenge", codeChallenge)
        .queryParam("code_challenge_method", "S256")
        .queryParam("response_type", "id_token")
        .queryParam("client_id", appConfig.githubClientId)
        .queryParam("state", state)
        .queryParam("redirect_uri", "http://localhost:8080/v1/api/auth/login/social/github/callback")
        .queryParam("scope", "user:email")
        .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                                 .location(URI.create(uri))
                                 .build();
    }

    @GetMapping("/login/social/github/callback")
    public TokenResponse loginGithubCallback(@QueryParam("code") String code, @QueryParam("state") String state){

        
        GitHubAuth2Response gitHubAuth2Response=webClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData("grant_type", "id_token")
                .with("client_id", appConfig.githubClientId)
                .with("client_secret", appConfig.githubClientSecret)
                .with("redirect_uri", "http://localhost:8080/v1/api/auth/login/social/github/callback")
                .with("code_verifier", redis.getCodeVerifier(state))
                .with("code", code))
                .retrieve()
                .bodyToMono(GitHubAuth2Response.class)
                .block();

        String email=getPrimaryEmailForGithubAuth(gitHubAuth2Response.getAccessToken());

        UserResponse userResponse=userservice.getUserByEmail(email);

        TokenResponse tokenResponse=new TokenResponse();
        tokenResponse.setAccessToken(jwtUtil.generateToken(userResponse.getName()));
        tokenResponse.setRefreshToken(gitHubAuth2Response.getAccessToken());
        tokenResponse.setIdToken(getPrimaryEmailForGithubAuth(gitHubAuth2Response.getAccessToken()));
        tokenResponse.setTokenType(userResponse.getName());
        return tokenResponse;
    }

    private String getPrimaryEmailForGithubAuth(String accessToken){
        List<GitHubEmailResponse> gitHubEmailResponses=webClient.get()
                .uri("https://api.github.com/user/emails")
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers->headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToFlux(GitHubEmailResponse.class)
                .collectList()
                .block();
        for(GitHubEmailResponse gitHubEmailResponse: gitHubEmailResponses){
            if(gitHubEmailResponse.getPrimary().booleanValue()){
                return gitHubEmailResponse.getEmail();
            }
        }

        throw new RuntimeException("no primary email found");
    }
}
