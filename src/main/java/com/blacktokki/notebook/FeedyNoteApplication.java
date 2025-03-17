package com.blacktokki.notebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FeedyNoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedyNoteApplication.class, args);
	}

}
