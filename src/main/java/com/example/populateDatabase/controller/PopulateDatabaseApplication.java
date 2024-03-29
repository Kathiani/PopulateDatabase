package com.example.populateDatabase.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class PopulateDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopulateDatabaseApplication.class, args);
	}

}
