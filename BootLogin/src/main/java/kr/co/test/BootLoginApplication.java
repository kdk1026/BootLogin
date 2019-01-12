package kr.co.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import config.AppConfig;
import config.WebMvcConfig;

@SpringBootApplication
@Import({
	WebMvcConfig.class, AppConfig.class
})
public class BootLoginApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BootLoginApplication.class, args);
	}
	
}

