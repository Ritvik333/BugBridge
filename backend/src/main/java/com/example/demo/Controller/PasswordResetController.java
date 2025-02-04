package com.example.demo.Controller;

import com.example.demo.Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public String requestPasswordReset(@RequestBody Map<String, String> request) {
        return passwordResetService.createPasswordResetToken(request.get("email"));
    }

    @GetMapping("/validate-reset-token")
    public boolean validateResetToken(@RequestParam String token) {
        return passwordResetService.validateToken(token);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> request) {
        return passwordResetService.resetPassword(request.get("token"), request.get("newPassword"));
    }
}
