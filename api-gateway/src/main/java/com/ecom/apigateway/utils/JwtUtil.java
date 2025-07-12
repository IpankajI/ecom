package com.ecom.apigateway.utils;

import io.jsonwebtoken.*;

import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Base64;


@Component
public class JwtUtil {

    private String publicKey;
    private String privateKey;
    public JwtUtil() throws NoSuchAlgorithmException{

        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    int accessExpirationMs=9600000;
    public String generateToken(String userName) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, Object> claims
                = new HashMap<>();
        return Jwts.builder()
                .subject(userName)
                .claims()
                .add(claims)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + accessExpirationMs))
                .issuer("NA")
                .and()
                .signWith(generateJwtKeyEncryption())
                .compact();
    }

    public PublicKey generateJwtKeyDecryption() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public PrivateKey generateJwtKeyEncryption() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    public Claims extractClaims(String authToken) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.parser().verifyWith(generateJwtKeyDecryption()).build().parseSignedClaims(authToken).getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUserName(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String subject) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        final String userName = extractUserName(token);
        return (userName.equals(subject) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractClaims(token, Claims::getExpiration);
    }
        

}
