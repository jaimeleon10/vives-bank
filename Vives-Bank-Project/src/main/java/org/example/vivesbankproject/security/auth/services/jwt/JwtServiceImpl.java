package org.example.vivesbankproject.security.auth.services.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.file.Files;
import java.security.KeyFactory;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration:86400}")

    private Long jwtExpiration;

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtServiceImpl(@Value("classpath:private_key_pkcs8.pem") Resource privateKeyResource,
                          @Value("classpath:public_key.pem") Resource publicKeyResource) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);
    }

    private RSAPrivateKey loadPrivateKey(Resource resource) throws Exception {
        String privateKeyPEM = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private RSAPublicKey loadPublicKey(Resource resource) throws Exception {
        String publicKeyPEM = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    @Override
    public String extractUserName(String token) {
        log.info("Extracting username from token " + token);
        return extractClaim(token, DecodedJWT::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user " + userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token " + token + " for user " + userDetails.getUsername());
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolvers) {
        log.info("Extracting claim from token " + token);
        final DecodedJWT decodedJWT = JWT.require(Algorithm.RSA256(publicKey,privateKey))
                .build()
                .verify(token);
        return claimsResolvers.apply(decodedJWT);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // Preparamos el token
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + (1000 * jwtExpiration));

        return JWT.create()
                .withHeader(createHeader())
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withClaim("extraClaims", extraClaims)
                .sign(algorithm);
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "RS256");
        return header;
    }

}