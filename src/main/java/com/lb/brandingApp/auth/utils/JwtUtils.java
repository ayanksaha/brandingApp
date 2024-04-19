package com.lb.brandingApp.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.brandingApp.auth.data.dto.common.PermissionDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.impl.compression.DeflateCompressionAlgorithm;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.lb.brandingApp.app.constants.ApplicationConstants.BEARER;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String base64EncodedSecretKey;

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.base64EncodedSecretKey));
    }

    public String generateIdToken(String username, String teamName, Set<PermissionDto> permissions) {
        Map<String, String> claims = null;
        try {
            claims = Map.of("username", username, "team", teamName, "permissions", getObjectMapper().writeValueAsString(permissions));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String subject = "bearer_token";
        return generateJwtToken(subject, claims);
    }

    public String generateJwtToken(String subject, Map<String, String> claims) {

        return Jwts.builder()
                .issuer("BrandingApp")
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(Instant.ofEpochSecond(System.currentTimeMillis())))
                .expiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .signWith(getKey())
                .compressWith(new DeflateCompressionAlgorithm())
                .compact();
    }

    public Claims parseIdToken(String jws) {
        Jws<Claims> jwt = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(jws);
        return jwt.getPayload();
    }

    public String getUsernameFromIdClaims(Claims claims) {
        return (String) claims.getOrDefault("username", null);
    }

    public Set<PermissionDto> getPermissionsFromIdClaims(Claims claims) {
        Set<PermissionDto> permissions;
        try {
            permissions = getObjectMapper().readValue((String)
                    claims.getOrDefault("permissions", null), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return permissions;
    }

    public static String getJws(String authHeader) {
        return authHeader.substring(BEARER.length() + 1);
    }
}
