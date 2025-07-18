package com.ecom.apigateway.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private int accessExpirationMs=9600000;

    public JwtUtil() throws NoSuchAlgorithmException{
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        privateKey=keyPair.getPrivate();
        publicKey=keyPair.getPublic();
    }
    public JwtUtil(String certPrivate, String certPublic){
        try{
            privateKey=getPrivate(getPemContent(certPrivate));
            publicKey=getPubKey(getPemContent(certPublic));
        }
        catch(IOException|NoSuchAlgorithmException|InvalidKeySpecException e){
            e.printStackTrace();
        }
    }

    private byte[] getPemContent(String cert) throws IOException{
        PemReader pemReader=new PemReader(new StringReader(cert));
        PemObject pemObject=pemReader.readPemObject();
        pemReader.close();
        return pemObject.getContent();
    }

    private PrivateKey getPrivate(byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
    private PublicKey getPubKey(byte[] publicKeyBytes)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

    private PublicKey getPublicKeyFromPrivatKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException{
        // Cast the PrivateKey to RSAPrivateCrtKey
        RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey) privateKey;
        // Create RSAPublicKeySpec using modulus and public exponent
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                rsaPrivateCrtKey.getModulus(),
                rsaPrivateCrtKey.getPublicExponent()
        );
        // Obtain KeyFactory instance for RSA
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // Generate PublicKey
        return keyFactory.generatePublic(publicKeySpec);
    }


    private void generateAndWriteKeys()throws NoSuchAlgorithmException{
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        PrivateKey privateKey=keyPair.getPrivate();
        PublicKey publicKey=keyPair.getPublic();
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream("id_rsa")))){
            pemWriter.writeObject(new PemObject("PRIVATE KEY", privateKey.getEncoded()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream("_id_rsa.pub")))) {
            pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKey.getEncoded()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String generateToken(String userName) {
        Map<String, Object> claims
                = new HashMap<>();
        return Jwts.builder()
                .subject(userName)
                .claims()
                .add(claims)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + accessExpirationMs))
                .issuer("https://www.ecom.com")
                .and()
                .signWith(privateKey)
                .compact();
    }

    public Claims extractClaims(String authToken) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(authToken).getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUserName(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractClaims(token, Claims::getExpiration);
    }
        

}
