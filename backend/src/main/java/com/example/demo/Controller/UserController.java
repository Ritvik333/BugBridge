package com.example.demo.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
import com.example.demo.dto.ResponseWrapper;
import com.example.demo.dto.UserDto;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsersWithBugs() {
        List<User> users = userService.getUsersWithBugs();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<String>> updateUser(@RequestParam Long userId, @RequestBody UserDto userDto) {
        ResponseWrapper<String> response = userService.updateUserAccount(userId, userDto);
    return ResponseEntity.ok(response);
    }

    @PostMapping("/update-email")
    public ResponseEntity<ResponseWrapper<String>> updateEmail(@RequestParam Long userId, @RequestParam String newEmail) {
        String result = userService.createEmailVerificationToken(userId, newEmail);
        return ResponseEntity.ok(new ResponseWrapper<>("success", result, null));
    }

}
