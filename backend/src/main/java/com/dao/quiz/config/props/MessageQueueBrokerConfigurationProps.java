package com.dao.quiz.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.messaging")
@Getter
@Setter
public class MessageQueueBrokerConfigurationProps {
    private String messageBroker;
}
