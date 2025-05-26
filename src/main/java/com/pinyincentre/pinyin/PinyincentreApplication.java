package com.pinyincentre.pinyin;

import com.pinyincentre.pinyin.entity.BaseEntity;
import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SendGridAutoConfiguration.class})
@EnableScheduling
public class PinyincentreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinyincentreApplication.class, args);
	}

}
