package com.atypon.nosql;

import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DemoApp {
	public static void main(String[] args) throws IOException, ParseException {
		SpringApplication.run(DemoApp.class, args);
	}
}
