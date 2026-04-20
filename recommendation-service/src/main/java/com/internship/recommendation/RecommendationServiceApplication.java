package com.internship.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Recommendation Service - The Brain of the System
 * 
 * This is the core service that orchestrates calls to other microservices
 * using Feign clients to calculate intelligent internship recommendations.
 * 
 * Key Features:
 * - Uses OpenFeign to communicate with User, Skill Assessment, and Internship services
 * - Implements weighted match score algorithm (40% + 30% + 20% + 10%)
 * - Provides ranked recommendations based on multiple factors
 * - No database - pure orchestration service
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // Enable Feign clients
public class RecommendationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendationServiceApplication.class, args);
        System.out.println("\n======================================");
        System.out.println("Recommendation Service Started!");
        System.out.println("Port: 8084");
        System.out.println("Feign Clients Enabled");
        System.out.println("======================================\n");
    }
}
