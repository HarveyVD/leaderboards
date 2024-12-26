package com.dao.quiz.config;

import com.dao.quiz.config.props.RedisConfigurationProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisConfigurationProps redisConfigurationProps;

    @Bean(destroyMethod = "destroy")
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(redisConfigurationProps.getHost(), redisConfigurationProps.getPort());
    }

    @Bean
    public RedisTemplate<String, String> stringRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisLockRegistry listingsLockRegistry() {
        return new RedisLockRegistry(connectionFactory(), "leaderboard");
    }
}
