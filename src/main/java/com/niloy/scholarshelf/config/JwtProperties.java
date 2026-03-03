package com.niloy.scholarshelf.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Strongly-typed configuration properties for JWT settings.
 * Eliminates IDE warnings about unknown 'app.jwt.*' keys.
 */
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /** Base64-encoded HMAC secret key. */
    private String secret;

    /** Token lifetime in milliseconds (default 24 h). */
    private long expirationMs = 86400000L;
}
