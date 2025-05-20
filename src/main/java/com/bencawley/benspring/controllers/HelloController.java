package com.bencawley.benspring.controllers;

// Import Spring annotations for REST controllers and mapping HTTP GET requests
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Marks this class as a REST controller, so Spring Boot knows to handle web requests here
@RestController
public class HelloController {

    // Maps HTTP GET requests to the root URL path "/" to this method
    @GetMapping("/")
    public String hello() {
        // When someone visits "/", this method returns a simple String response
        // Spring Boot sends this String as the HTTP response body directly
        return "Hello from Spring Boot!";
    }
}
