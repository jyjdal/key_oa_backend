package com.key.oa.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

/**
 * @author 孙强
 * Jwt基本功能的封装
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "key-oa.jwt")
public class JwtUtil {
    /**
     * 默认有效时间，默认1天
     */
    private Integer ttlByMinute = 60 * 24;

    /**
     * 证书签发方
     */
    private String issuer = "jyjdal";

    /**
     * 加密时使用的盐
     */
    private String salt;

    public String generate(String subject) {
        return generateWithClaims(subject, null);
    }

    public String generateWithClaims(String subject, Map<String, Object> claims) {
        if (Objects.isNull(claims)) {
            return builder().setSubject(subject).compact();
        }
        return builder().setClaims(claims).setSubject(subject).compact();
    }

    public Claims parse(String token) {
        return parser().parseClaimsJws(token).getBody();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(salt.getBytes(StandardCharsets.UTF_8)));
    }

    private JwtBuilder builder() {
        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                // 默认失效时间为1天
                .setExpiration(DateUtils.addMinutes(new Date(), ttlByMinute))
                .setId(UUID.randomUUID().toString())
                .signWith(key());
    }

    private JwtParser parser() {
        return Jwts.parserBuilder().setSigningKey(key()).build();
    }
}
