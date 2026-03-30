package com.ecommerce.service;

import com.ecommerce.model.PasswordResetToken;
import com.ecommerce.model.User;
import com.ecommerce.repository.PasswordResetTokenRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${frontend.url:http://localhost:3001}")
    private String frontendUrl;

    @Transactional
    public void sendResetLink(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("No account found with that email");
        }

        // Delete any existing token for this email
        tokenRepository.deleteByEmail(email);

        // Generate new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, email);
        tokenRepository.save(resetToken);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("farkornam3800@gmail.com");
        message.setTo(email);
        message.setSubject("ShopEase - Password Reset Request");
        message.setText(
                "Hello " + user.getName() + ",\n\n" +
                        "You requested to reset your password.\n\n" +
                        "Click the link below to reset your password:\n" +
                        frontendUrl + "/reset-password?token=" + token + "\n\n" +
                        "This link expires in 1 hour.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "ShopEase Team"
        );
        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token has expired. Please request a new reset link.");
        }

        User user = userRepository.findByEmail(resetToken.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}