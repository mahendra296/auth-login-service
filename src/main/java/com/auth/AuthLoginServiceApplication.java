package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthLoginServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthLoginServiceApplication.class, args);
    }
}
