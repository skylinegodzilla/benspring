package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.HelloDTO;
import com.bencawley.benspring.dtos.HelloResponseDTO;
import org.springframework.web.bind.annotation.*;
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

    // Handles POST requests to "/request/data"
    @PostMapping("/request/data")
    public HelloResponseDTO handleData(@RequestBody HelloDTO request) {
        String input = request.getInput();
        if (input == null) {
            // Return some default or error message instead of reversing null
            return new HelloResponseDTO("Input was null");
        }
        String reversed = new StringBuilder(input).reverse().toString();
        return new HelloResponseDTO(reversed);
    }
}
