package com.example.demo.Service;

import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.PasswordResetTokenRepository;
import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ 1. Generate and send password reset token
    public String createPasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return "User not found";
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(1));

        tokenRepository.save(resetToken);

        sendResetEmail(user.getEmail(), token);
        return "Password reset email sent";
    }

    private void sendResetEmail(String email, String token) {
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetLink);
        mailSender.send(message);
    }

    // ✅ 2. Validate token before password reset
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        return tokenOpt.isPresent() && tokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    // ✅ 3. Reset password
    public String resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (!tokenOpt.isPresent() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Invalid or expired token";
        }

        User user = tokenOpt.get().getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(tokenOpt.get());

        return "Password reset successful";
    }
}
