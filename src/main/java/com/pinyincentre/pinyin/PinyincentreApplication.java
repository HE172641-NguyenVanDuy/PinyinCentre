package com.pinyincentre.pinyin;

import com.pinyincentre.pinyin.entity.BaseEntity;
import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SendGridAutoConfiguration.class})
@EnableJpaAuditing
@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAsync
@EnableScheduling
public class PinyincentreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinyincentreApplication.class, args);
	}

}
