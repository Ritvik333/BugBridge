package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import com.example.demo.Service.RegistrationService;

@RestController
public class RegistrationController {
    
    @Autowired
    private UserRepository myAppUserRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping(value = "/req/signup", consumes = "application/json")
    public User createUser(@RequestBody User user) {
        try {
            // Call the registration service to handle the user creation
            return registrationService.registerUser(user);
        } catch (IllegalArgumentException e) {
            // Handle validation error (returning a custom message)
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
    
}
