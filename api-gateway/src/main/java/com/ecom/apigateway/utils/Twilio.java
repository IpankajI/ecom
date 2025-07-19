package com.ecom.apigateway.utils;

import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.dto.OtpResponse;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class Twilio {
    private final WebClient webClient;

    public String requestOTP(String phoneNumber){
        OtpResponse otpResponse=webClient
            .post()
            .uri("https://verify.twilio.com/v2/Services/VA5becb7b477c1d0ac1b0c34374e98e6e3/Verifications")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("AUTHORIZATION", getBasicAuth())
            .body(BodyInserters.fromFormData("To", phoneNumber).with("Channel", "sms"))
            .retrieve()
            .bodyToMono(OtpResponse.class)
            .block();
        if(otpResponse==null || !otpResponse.getTo().equals(phoneNumber) || !otpResponse.getStatus().equals("pending")){
            return "failed";
        }
        return "success";
    }

    public boolean validateOtp(String phoneNumber, String otp){
        OtpResponse otpResponse=webClient
            .post()
            .uri("https://verify.twilio.com/v2/Services/VA5becb7b477c1d0ac1b0c34374e98e6e3/VerificationCheck")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("AUTHORIZATION", getBasicAuth())
            .body(BodyInserters.fromFormData("To", phoneNumber).with("Code", otp))
            .retrieve()
            .bodyToMono(OtpResponse.class)
            .block();
        return otpResponse!=null && otpResponse.getTo().equals(phoneNumber) && otpResponse.isValid() && otpResponse.getStatus().equals("approved");
    }

    private String getBasicAuth(){
        String basicAuth=System.getenv("TWILIO_USERNAME")+":"+System.getenv("TWILIO_PASSWORD");
		return "Basic "+Base64.getEncoder().encodeToString(basicAuth.getBytes());
    }
}
