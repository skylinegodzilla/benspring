package com.bencawley.benspring.controllers;
/*
    Hello
    This is a sandbox project to build a simple back end.

    some things to keep in mind

    DTO is like our response objects in swift. except the dto will be turned in to a JSON not created from a JSON but yeah its the data prepared to be sent over the REST api

    An entity is the data that stores the information that is pulled from the database

    A controller handles the api endpoint and prepears the dto's to be sent using the mapper.

    A mapper maps dtos to entitys.


 */



// Import DTO classes for request and response data structures
import com.bencawley.benspring.dtos.HelloDTO;
import com.bencawley.benspring.dtos.HelloResponseDTO;

// Import Spring annotations for building REST controllers and mapping HTTP requests
import org.springframework.web.bind.annotation.*;

@RestController  // Indicates this class handles REST API requests and returns response bodies directly
public class HelloController {

    // Maps HTTP GET requests for the root URL "/" to this method
    @GetMapping("/")
    public String hello() {
        // Returns a simple plain-text greeting message as the HTTP response body
        return "Hello from Spring Boot!";
    }

    // Maps HTTP POST requests sent to "/request/data" to this method
    @PostMapping("/api/data")
    public HelloResponseDTO handleData(
            // Instructs Spring to parse JSON request body into a HelloDTO object
            @RequestBody HelloDTO request) {

        // Extract the 'input' field from the incoming request object
        String input = request.getInput();

        // Check if input is null to avoid processing errors
        if (input == null) {
            // Return a response DTO with an error message if input is missing
            return new HelloResponseDTO("Input was null");
        }

        // Reverse the input string using StringBuilder for demonstration
        String reversed = new StringBuilder(input).reverse().toString();

        // Wrap the reversed string inside a response DTO and return it
        // Spring will automatically serialize this object as JSON in the HTTP response
        return new HelloResponseDTO(reversed);
    }
}