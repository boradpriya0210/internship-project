package com.internship.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InternshipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternshipServiceApplication.class, args);
        System.out.println("\n======================================");
        System.out.println("Internship Service Started!");
        System.out.println("Port: 8083");
        System.out.println("======================================\n");
    }
}
