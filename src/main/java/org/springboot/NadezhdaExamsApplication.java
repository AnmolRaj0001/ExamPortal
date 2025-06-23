package org.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NadezhdaExamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NadezhdaExamsApplication.class, args);
	}

}
