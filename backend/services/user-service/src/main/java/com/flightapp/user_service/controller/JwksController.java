package com.flightapp.user_service.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

@RestController
public class JwksController {

    @GetMapping("/api/v1.0/flight/auth/.well-known/jwks.json")
    public Map<String, Object> getJwks() throws Exception {
        ClassPathResource resource = new ClassPathResource("certs/public_key.der");
        byte[] keyBytes = resource.getInputStream().readAllBytes();
        
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(spec);

        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID("flight-booking-key-id")
                .build();

        return new JWKSet(jwk).toJSONObject();
    }
}
