package com.dao.quiz.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "application.security.jwt")
@Getter
@Setter
public class JWTConfigurationProps {
    private String secret;
    private String issuer;
    private Duration accessTokenExpiration;
}
