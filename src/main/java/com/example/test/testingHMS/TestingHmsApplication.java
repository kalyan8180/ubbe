package com.example.test.testingHMS;


import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class TestingHmsApplication  implements CommandLineRunner{

	
	@Autowired
	DataSource dataSource;
	public static Logger Logger=LoggerFactory.getLogger(TestingHmsApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(TestingHmsApplication.class, args);
		
		
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Data Source = " +dataSource);
		
	}
	
	
	
}
