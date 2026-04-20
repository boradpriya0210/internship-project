package com.internship.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service Application
 * 
 * This microservice manages student profiles, education, skills, and interests.
 * It provides REST APIs for student registration and profile management.
 * 
 * Key Responsibilities:
 * - Student registration and profile management
 * - Managing student skills and proficiency levels
 * - Managing student interests and preferences
 * - Providing student data to other services (via Feign)
 * 
 * Database: user_service_db (MySQL)
 * Port: 8081
 * Service Name: user-service
 * 
 * API Endpoints (via API Gateway):
 * - POST /api/users/register - Register new student
 * - GET /api/users/{id} - Get student profile
 * - PUT /api/users/{id} - Update student profile
 * - GET /api/users/{id}/skills - Get student skills
 * - GET /api/users/{id}/interests - Get student interests
 * 
 * @author Smart Internship System
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("\n======================================");
        System.out.println("User Service Started Successfully!");
        System.out.println("Port: 8081");
        System.out.println("Service registered with Eureka");
        System.out.println("======================================\n");
    }
}
