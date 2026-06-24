package com.freshfood.backend.security;

import com.freshfood.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Long expiresIn;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.expires-in}") Long expiresIn) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expiresIn = expiresIn;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(String.valueOf(user.getId()))
                .claim("userId", user.getId())
                .claim("openid", user.getOpenid())
                .claim("nickName", user.getNickName())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
