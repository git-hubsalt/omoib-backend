package com.githubsalt.omoib.global.config.security;

import com.githubsalt.omoib.global.dto.CustomOAuth2User;
import com.githubsalt.omoib.global.dto.CustomUserInfoDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    private final Key key;
    private final long accessTokenExpireTime;

    public JwtProvider(
        @Value("${jwt.token.secret-key}") String secretKey,
        @Value("${jwt.access-token.expire-time}") long accessTokenExpireTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpireTime = accessTokenExpireTime;
    }

    //Access Token 생성
    public String createAccessToken(CustomUserInfoDTO userInfo) {
        return createToken(userInfo, accessTokenExpireTime);
    }

    //JWT 생성
    private String createToken(CustomUserInfoDTO userInfo, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("userId", userInfo.getUserId());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(now.toInstant()))
            .setExpiration(Date.from(tokenValidity.toInstant()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    //Token에서 User ID 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    //JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    //JWT Claims 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public Authentication getAuthentication(String token) {
        Long userId = getUserId(token);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        CustomOAuth2User principal = new CustomOAuth2User(userId, authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
