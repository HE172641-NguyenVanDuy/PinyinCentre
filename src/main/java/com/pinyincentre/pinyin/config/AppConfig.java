package com.pinyincentre.pinyin.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${spring.sendgrid.api-key}")
    private String sendgridApiKey;

    @Bean
    public SendGrid sendgrid() {
        if (sendgridApiKey == null || sendgridApiKey.isBlank()) {
            throw new IllegalArgumentException("SendGrid API Key is missing!");
        }
        return new SendGrid(sendgridApiKey);
    }

}
