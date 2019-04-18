package com.ef.wss.newscollect.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.ef.wss.newscollect.*")
@MapperScan("com.ef.wss.newscollect.mapper")
public class AppNewsCollect {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplicationBuilder(AppNewsCollect.class).bannerMode(Banner.Mode.OFF).build();
		app.run(args);  
	int a = 12_21;
	}

}
