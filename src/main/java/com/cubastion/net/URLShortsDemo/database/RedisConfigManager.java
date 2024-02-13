package com.cubastion.net.URLShortsDemo.database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfigManager {
    @Value("${redis.HOST}")
    private String redisHost;

    @Value("${redis.PORT}")
    private int redisPort;

    @Value("${redis.USERNAME}")
    private String username;

    @Value("${redis.PASSWORD}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(this.redisHost, this.redisPort);
        configuration.setUsername(this.username);
        configuration.setPassword(this.password);
        return new LettuceConnectionFactory(configuration);
    }

}
