package com.ecom.apigateway.utils;

import io.jsonwebtoken.*;

import org.apache.commons.codec.binary.Base64;
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


@Component
public class JwtUtil {

    private String publicKey;
    private String privateKey;
    public JwtUtil() throws NoSuchAlgorithmException{

        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        privateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
        System.out.println(".......public key: "+publicKey); 
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
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public PrivateKey generateJwtKeyEncryption() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.decodeBase64(privateKey);
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

/* 
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.KeyPair;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureAlgorithm;

@Component
public class JwtUtil {

    public String generateToken(String subject) {
        Map<String, Object> claims
                = new HashMap<>();
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuer("NA")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 60*10*1000))
                .and()
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        byte[] decode
                = Decoders.BASE64.decode(getSecretKey());

        return Keys.hmacShaKeyFor(decode);
    }


    public String getSecretKey() {
        return "RqxPOuVfHoBA8Uq40MhJvfY6qEHOOWWvg6N9W9vt23s=";
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String subject) {
        final String userName = extractUserName(token);
        return (userName.equals(subject) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}

*/
