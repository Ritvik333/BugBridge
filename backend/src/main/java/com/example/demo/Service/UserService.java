package com.example.demo.Service;

import com.example.demo.dto.CredentialsDto;
import com.example.demo.dto.SignUpDto;
import com.example.demo.dto.UserDto;
import com.example.demo.Model.User;
import com.example.demo.exceptions.AppException;
import com.example.demo.mappers.UserMapper;

import jakarta.persistence.EntityNotFoundException;

import com.example.demo.Model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        if (userDto == null) {
            throw new AppException("User data cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new AppException("Email is required", HttpStatus.BAD_REQUEST);
        }

        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            throw new AppException("Username is required", HttpStatus.BAD_REQUEST);
        }

        if (userDto.getPassword() == null || userDto.getPassword().length == 0) {
            throw new AppException("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    public UserDto findByLogin(String login) {
        User user = userRepository.findByEmail(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }
        return user.get();
    }

    public List<User> getUsersWithBugs() {
        return userRepository.findUsersWithBugs();
    }
}