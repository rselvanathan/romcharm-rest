package com.romcharm.authorization;

import com.romcharm.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JWTUtil {

    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE = "role";

    @Value("${jwtSecret}")
    private String jwtSecret;

    Optional<String> getTokenUsername(String token) {
        return getTokenField(token, CLAIM_USERNAME);
    }

    Optional<String> getTokenRole(String token) {
        return getTokenField(token, CLAIM_ROLE);
    }

    public String generateToken(User userRole) {
        Map<String, Object> map = new HashMap<>();
        map.put(CLAIM_USERNAME, userRole.getUsername());
        map.put(CLAIM_ROLE, userRole.getRole());
        return generateToken(map);
    }

    private Optional<String> getTokenField(String token, String field) {
        Optional<Claims> claimsFromToken = getClaimsFromToken(token);
        if(claimsFromToken.isPresent()) {
            return Optional.of(claimsFromToken.get().get(field, String.class));
        }
        return Optional.empty();
    }

    private Optional<Claims> getClaimsFromToken(String token) {
        try{
            return Optional.of(Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String generateToken(Map<String, Object> map) {
        return Jwts.builder()
                .setClaims(map)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Tests only
     */
    void setJwtSecret(String secret) {
        jwtSecret = secret;
    }
}
