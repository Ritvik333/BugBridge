package com.example.demo.Service;

import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.PasswordResetTokenRepository;
import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

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

    // Generate and send password reset token
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
        try{
            String tokenToBeValidated = token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String emailContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9;'>"
                    + "<h2 style='color: #333;'>Password Reset Request</h2>"
                    + "<p style='font-size: 16px;'>Hello,</p>"
                    + "<p style='font-size: 16px;'>We received a request to reset your password. Use the token below to proceed:</p>"
                    + "<div style='text-align: center; margin: 20px 0; padding: 10px; background-color: #eee; border-radius: 5px; font-size: 18px; font-weight: bold; letter-spacing: 1px;'>"
                    + tokenToBeValidated + "</div>"
                    + "<p style='font-size: 14px; color: #777;'>Copy this token and paste it into the password reset form.</p>"
                    + "<p style='font-size: 14px; color: #777;'>This token will expire in 1 hour.</p>"
                    + "<p style='font-size: 14px; color: #777;'>If you did not request this, please ignore this email.</p>"
                    + "<p style='font-size: 14px; color: #777;'>Thank you,<br>The Support Team</p>"
                    + "</div>";

            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setText(emailContent, true); // `true` enables HTML content

            mailSender.send(message);  // This sends the email dynamically
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle error properly in production
        }
    }

    // Validate token before password reset
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        return tokenOpt.isPresent() && tokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    // Reset password
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
