package com.nailpos.nailposapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NailPosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NailPosApiApplication.class, args);
	}

}
