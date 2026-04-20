package com.internship.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway - Central Entry Point
 * 
 * This service acts as a single entry point for all client requests.
 * All frontend requests go through this gateway, which then routes them
 * to the appropriate microservice.
 * 
 * Key Features:
 * - Request Routing: Routes requests to appropriate microservices
 * - Load Balancing: Distributes load across multiple service instances
 * - Service Discovery: Uses Eureka to find available services
 * - CORS Handling: Manages cross-origin requests for frontend
 * - Centralized Access: Single point for all API calls
 * 
 * Route Mapping:
 * - /api/users/** → User Service
 * - /api/assessments/** → Skill Assessment Service
 * - /api/internships/** → Internship Service
 * - /api/recommendations/** → Recommendation Service
 * - /api/notifications/** → Notification Service
 * 
 * Gateway URL: http://localhost:8080
 * 
 * @author Smart Internship System
 */
@SpringBootApplication
@EnableDiscoveryClient  // Enables service discovery via Eureka
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("\n======================================");
        System.out.println("API Gateway Started Successfully!");
        System.out.println("Gateway URL: http://localhost:8080");
        System.out.println("All requests should be routed through this gateway");
        System.out.println("======================================\n");
    }
}
