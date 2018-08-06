package com.example.testapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestappApplication implements CommandLineRunner {
	private final MainService mainService;

    @Autowired
    public TestappApplication(MainService mainService) {
        this.mainService = mainService;
    }

    public static void main(String[] args) {
		SpringApplication.run(TestappApplication.class, args);
	}

	@Override
	public void run(String... args) {
    	System.out.println(mainService.exec(args));
	}
}
