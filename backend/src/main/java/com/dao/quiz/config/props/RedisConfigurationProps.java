package com.dao.quiz.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.storage.redis")
@Getter
@Setter
public class RedisConfigurationProps {
    private String host;
    private int port;
}
