package com.pinyincentre.pinyin.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cache.redis.key.prefix")
public class RedisKeyProperties {
    private String access;
    private String refresh;
    private String logout;
    private String reset;
    private String active;
    private String payment;
}

