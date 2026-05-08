package com.vinicius.coretech.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Component
public class RSAKeyProperties {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RSAKeyProperties(
            @Value("${RSA_PUBLIC_KEY}") String publicKeyBase64,
            @Value("${RSA_PRIVATE_KEY}") String privateKeyBase64
    ) throws Exception {

        byte[] pubBytes = Base64.getDecoder().decode(publicKeyBase64);
        byte[] privBytes = Base64.getDecoder().decode(privateKeyBase64);

        this.publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(pubBytes));

        this.privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privBytes));
    }
}
