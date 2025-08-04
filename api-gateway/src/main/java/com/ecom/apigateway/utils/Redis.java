package com.ecom.apigateway.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Component
public class Redis {
    
    private Map<String, CodeVerifier> stateToCodeVerifier;

    public Redis(){
        stateToCodeVerifier=new HashMap<>();
    }
    public void set(String state, String codeVerifier, String codeChallenge){
        stateToCodeVerifier.put(state, new CodeVerifier(codeVerifier, codeChallenge));
    }


    public String getCodeVerifier(String state){
        return stateToCodeVerifier.get(state).getCodeVerifier();
    }

    public String getCodeChallenge(String state){
        return stateToCodeVerifier.get(state).getCodeChallenge();
    }

}


@AllArgsConstructor
@Getter
class CodeVerifier{
    private String codeVerifier;
    private String codeChallenge;
}