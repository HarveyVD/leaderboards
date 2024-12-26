package com.dao.quiz.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.messaging.activemq")
@Getter
@Setter
public class ActiveMqBrokerConfigurationProps {
    private String host;
    private int port;
    private String username;
    private String password;
}
