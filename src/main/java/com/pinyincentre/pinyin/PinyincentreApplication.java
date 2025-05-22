package com.pinyincentre.pinyin;

import com.pinyincentre.pinyin.entity.BaseEntity;
import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EntityListeners(BaseEntity.class)
public class PinyincentreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinyincentreApplication.class, args);
	}

}
