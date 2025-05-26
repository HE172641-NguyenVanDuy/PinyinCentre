package com.pinyincentre.pinyin;

import com.pinyincentre.pinyin.entity.BaseEntity;
import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PinyincentreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinyincentreApplication.class, args);
	}

}
