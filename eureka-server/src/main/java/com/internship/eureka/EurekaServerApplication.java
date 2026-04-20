package com.internship.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Service Registry
 * 
 * This is the service discovery server that allows all microservices to register
 * themselves and discover other services. All microservices in the system will
 * register with this Eureka server.
 * 
 * Key Features:
 * - Service Registration: Microservices register their instances
 * - Service Discovery: Services can find each other dynamically
 * - Health Monitoring: Tracks the health of registered services
 * - Load Balancing: Provides multiple instances for load balancing
 * 
 * Access the Eureka Dashboard at: http://localhost:8761
 * 
 * @author Smart Internship System
 */
@SpringBootApplication
@EnableEurekaServer  // Enables this application as a Eureka Server
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        System.out.println("\n======================================");
        System.out.println("Eureka Server Started Successfully!");
        System.out.println("Dashboard: http://localhost:8761");
        System.out.println("======================================\n");
    }
}
