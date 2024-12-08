package org.example.vivesbankproject.rest.auth.services.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import java.security.KeyFactory;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Implementation of the JwtService interface, responsible for JWT token operations like creation, validation,
 * and extraction of user information. Uses RSA-based keys for security.
 *  @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 *  @version 1.0-SNAPSHOT
 */
@Service
@Slf4j

public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    /**
     * Constructor to initialize RSA private and public keys from specified resources.
     *
     * @param privateKeyResource Path to the RSA private key file.
     * @param publicKeyResource Path to the RSA public key file.
     * @throws Exception if there is an issue with key loading or parsing.
     */
    public JwtServiceImpl(@Value("classpath:private_key_pkcs8.pem") Resource privateKeyResource,
                          @Value("classpath:public_key.pem") Resource publicKeyResource) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);
    }

    /**
     * Loads an RSA private key from a PEM resource.
     *
     * @param resource The resource pointing to the private key PEM file.
     * @return RSAPrivateKey loaded from the given file.
     * @throws Exception if unable to decode or parse the private key.
     */
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

    /**
     * Loads an RSA public key from a PEM resource.
     *
     * @param resource The resource pointing to the public key PEM file.
     * @return RSAPublicKey loaded from the given file.
     * @throws Exception if unable to decode or parse the public key.
     */
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
    @Operation(summary = "Extract username from JWT token",
            description = "Extracts the username (subject) from the provided JWT token.")
    @ApiResponse(responseCode = "200", description = "Username extracted successfully")
    public String extractUserName(String token) {
        log.info("Extracting username from token " + token);
        return extractClaim(token, DecodedJWT::getSubject);
    }

    @Override
    @Operation(summary = "Generate JWT token",
            description = "Generates a JWT token for the provided authenticated user details.")
    @ApiResponse(responseCode = "200", description = "JWT token generated successfully")
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user " + userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    @Operation(summary = "Validate JWT token",
            description = "Validates if the provided JWT token is valid and has not expired.")
    @ApiResponse(responseCode = "200", description = "Token is valid")
    @ApiResponse(responseCode = "401", description = "Invalid token or expired token")
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token " + token + " for user " + userDetails.getUsername());
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extracts a claim from the provided JWT token using a given resolver function.
     *
     * @param token            JWT token to decode.
     * @param claimsResolvers Function to resolve the desired claim from the token's decoded claims.
     * @param <T>              Type of the claim expected to extract.
     * @return Extracted claim value.
     */
    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolvers) {
        log.info("Extracting claim from token " + token);
        final DecodedJWT decodedJWT = JWT.require(Algorithm.RSA256(publicKey, privateKey))
                .build()
                .verify(token);
        return claimsResolvers.apply(decodedJWT);
    }

    /**
     * Generates a JWT token for the user details with optional extra claims.
     *
     * @param extraClaims  Additional claims to include in the token payload.
     * @param userDetails  The authenticated user's details.
     * @return Generated JWT token as a string.
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
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

    /**
     * Checks whether the token is expired.
     *
     * @param token JWT token.
     * @return true if expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    /**
     * Extracts expiration date from a JWT token.
     *
     * @param token JWT token.
     * @return Expiration date from token's claims.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    /**
     * Creates the JWT token header.
     *
     * @return Map representing JWT header properties.
     */
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "RS256");
        return header;
    }
}