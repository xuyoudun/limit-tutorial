package org.iteration.tutorial.controller;

import org.iteration.tutorial.aspectj.RateLimiter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/limit")
public class ApiController {

    @GetMapping("/api1")
    @RateLimiter(windowSize = 10, maxRequests = 50)
    public String api1(String userId) {
        return "Request successful!";
    }

    @PostMapping("/api2")
    @RateLimiter(windowSize = 10, maxRequests = 50)
    public String api2(String userId) {
        return "Request successful!";
    }

    @PutMapping("/api3")
    @RateLimiter(windowSize = 10, maxRequests = 50)
    public String api3(String userId) {
        return "Request successful!";
    }
}