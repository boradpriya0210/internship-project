package com.internship.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SkillAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillAssessmentApplication.class, args);
        System.out.println("\n======================================");
        System.out.println("Skill Assessment Service Started!");
        System.out.println("Port: 8082");
        System.out.println("======================================\n");
    }
}
