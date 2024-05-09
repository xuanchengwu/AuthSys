package com.example.authsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"Controller", "Service", "Model", "config", "Security"})
public class AuthSysApplication {

    public static void main(String[] args) {

        SpringApplication.run(AuthSysApplication.class, args);
    }

}



