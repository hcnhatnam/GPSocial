package com.iplocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestServiceApplication {

    public static void main(String[] args) {
        System.getProperties().put("server.port", 8765);
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
