package com.hospital.backend.config.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return Objects.requireNonNull(getClaimsFromToken(token)).getSubject();
    }

    public boolean validateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null && claims.getExpiration().after(new Date());
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
