package com.dao.quiz.security.providers;

import com.dao.quiz.config.props.JWTConfigurationProps;
import com.dao.quiz.exceptions.NotAccessTokenException;
import com.dao.quiz.exceptions.UserNotFoundException;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.repositories.UserRepository;
import com.dao.quiz.security.services.AuthUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JWTTokenProvider {
    private final JWTConfigurationProps jwtConfigurationProps;
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String FIELD_SESSION_ID = "sessionId";
    public static final String FIELD_USER_ID = "userId";
    private final Key key;
    private final UserRepository userRepository;

    public JWTTokenProvider(JWTConfigurationProps jwtConfigurationProps,
                            UserRepository userRepository) {
        this.jwtConfigurationProps = jwtConfigurationProps;
        this.userRepository = userRepository;
        key = Keys.hmacShaKeyFor(jwtConfigurationProps.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user, String sessionId) {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("type", TOKEN_TYPE_ACCESS);
        claimsMap.put(FIELD_USER_ID, user.getId());
        claimsMap.put(FIELD_SESSION_ID, sessionId);
        Claims claims = Jwts.claims(claimsMap);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtConfigurationProps.getIssuer())
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfigurationProps.getAccessTokenExpiration().toMillis()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication parseAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(jwtConfigurationProps.getIssuer())
                .build()
                .parseClaimsJws(token)
                .getBody();
        if (!TOKEN_TYPE_ACCESS.equalsIgnoreCase(String.valueOf(claims.get("type")))) {
            throw new NotAccessTokenException("Provided token is not a type of 'access' one");
        }
        User user = userRepository.findByUsername(claims.getSubject())
                .orElseThrow(UserNotFoundException::new);
        AuthUserDetails principal = new AuthUserDetails(user);
        return new UsernamePasswordAuthenticationToken(principal, null, null);
    }
}
