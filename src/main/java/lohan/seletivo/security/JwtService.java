package lohan.seletivo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecurityProperties properties;
    private final SecretKey signingKey;

    public JwtService(SecurityProperties properties) {
        this.properties = properties;
        this.signingKey = buildKey(properties.getJwt().getSecret());
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, properties.getJwt().getAccessTokenTtl().toSeconds(), "access");
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, properties.getJwt().getRefreshTokenTtl().toSeconds(), "refresh");
    }

    public boolean isTokenValid(String token, UserDetails userDetails, String expectedType) {
        Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();
        return userDetails.getUsername().equals(username)
                && expectedType.equals(type)
                && expiration != null
                && expiration.after(new Date());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Instant extractExpiration(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration != null ? expiration.toInstant() : null;
    }

    public String extractTokenId(String token) {
        return extractAllClaims(token).getId();
    }

    private String generateToken(UserDetails userDetails, long ttlSeconds, String type) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(ttlSeconds);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setId(java.util.UUID.randomUUID().toString())
                .setIssuer(properties.getJwt().getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("type", type)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey buildKey(String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret deve ter pelo menos 32 caracteres");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
