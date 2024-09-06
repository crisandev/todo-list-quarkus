package org.agilesoft.authentication;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Singleton
public class AuthenticationJWTService {

    private final String ISSUER = "authentication";
    private final String SUBJECT = "authentication";
    private final long EXPIRATION_TIME_IN_HOURS = 1;

    public String generateJWT(String username) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(EXPIRATION_TIME_IN_HOURS, ChronoUnit.HOURS);

        return Jwt.issuer(ISSUER).subject(SUBJECT).claim("username", username).groups("userRegistered").expiresAt(expirationTime).sign();
    }
}
