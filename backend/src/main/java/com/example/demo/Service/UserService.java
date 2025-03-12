package com.example.demo.Service;

import com.example.demo.dto.CredentialsDto;
import com.example.demo.dto.ResponseWrapper;
import com.example.demo.dto.SignUpDto;
import com.example.demo.dto.UserDto;
import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.PasswordResetTokenRepository;
import com.example.demo.Model.User;
import com.example.demo.exceptions.AppException;
import com.example.demo.mappers.UserMapper;

import jakarta.persistence.EntityNotFoundException;

import com.example.demo.Model.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

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

    public ResponseWrapper<String> updateUserAccount(Long userId, UserDto updatedUserDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        boolean updated = false;

        // ✅ Update Username
        if (updatedUserDto.getUsername() != null && !updatedUserDto.getUsername().isEmpty()) {
            user.setUsername(updatedUserDto.getUsername());
            updated = true;
        }

        // ✅ Send OTP for Email Change (No immediate update)
        if (updatedUserDto.getEmail() != null && !updatedUserDto.getEmail().isEmpty() &&
                !updatedUserDto.getEmail().equals(user.getEmail())) {
            createEmailVerificationToken(userId, updatedUserDto.getEmail());
            return new ResponseWrapper<>("success", "Email verification OTP Sent", null);
        }

        // ✅ Directly Update Password (No OTP Required)
        if (updatedUserDto.getPassword() != null && !updatedUserDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
            updated = true;
            userRepository.save(user);
            return new ResponseWrapper<>("success", "Password updated successfully", null);
        }

        if (!updated) {
            throw new AppException("No valid fields to update", HttpStatus.BAD_REQUEST);
        }

        userRepository.save(user);
        return new ResponseWrapper<>("success", "User updated successfully", null);
    }

    // ✅ Minimal Email Verification Token Generation
    public String createEmailVerificationToken(Long userId, String newEmail) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return "User not found";
        }

        User user = userOpt.get();

        // ✅ Remove existing token before creating a new one
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String otp = "123456"; // Hardcoded OTP for test simplicity

        PasswordResetToken verificationToken = new PasswordResetToken(otp, user, LocalDateTime.now().plusHours(1), newEmail);
        tokenRepository.save(verificationToken);

        return "Verification email sent";
    }

    // ✅ Minimal Email Verification Functionality
    public boolean verifyEmail(String otp, Long userId) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(otp);
        if (!tokenOpt.isPresent()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(LocalDateTime.now()) || !token.getUser().getId().equals(userId)) {
            return false;
        }

        User user = token.getUser();
        user.setEmail(token.getNewEmail());
        userRepository.save(user);
        tokenRepository.delete(token);

        return true;
    }
}