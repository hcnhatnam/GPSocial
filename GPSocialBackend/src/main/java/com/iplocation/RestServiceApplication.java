package com.iplocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class RestServiceApplication {

    public static void main(String[] args) {
        System.getProperties().put("server.port", 8765);
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
