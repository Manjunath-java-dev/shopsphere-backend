package com.shopsphere.service;

import com.shopsphere.entity.PasswordResetToken;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.PasswordResetTokenRepository;
import com.shopsphere.repositoy.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender) {

        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public void forgotPassword(String email) {

        // 1. Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with this email not found"
                        ));

        // 2. Generate unique token
        String token = UUID.randomUUID().toString();

        // 3. Create reset token
        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now().plusMinutes(15)
                        )
                        .used(false)
                        .build();

        passwordResetTokenRepository.deleteByUserId(user.getId());

        // 4. Save token
        passwordResetTokenRepository.save(resetToken);

        // 5. Create reset link
        String resetLink =
                "http://127.0.0.1:5500/pages/reset-password.html?token="
                        + token;

        // 6. Send email
        sendResetEmail(user.getEmail(), resetLink);
    }

    private void sendResetEmail(String email, String resetLink) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(email);

            helper.setSubject("ShopSphere - Password Reset");

            String htmlContent =
                    "<html>" +
                            "<body>" +

                            "<h2>ShopSphere - Password Reset</h2>" +

                            "<p>Hello,</p>" +

                            "<p>You requested to reset your ShopSphere password.</p>" +

                            "<p>Click the button below to reset your password:</p>" +

                            "<p>" +
                            "<a href=\"" + resetLink + "\">" +
                            "Reset Password" +
                            "</a>" +
                            "</p>" +

                            "<p>This link will expire in 15 minutes.</p>" +

                            "<p>If you did not request this, please ignore this email.</p>" +

                            "<p>Regards,<br>" +
                            "ShopSphere Team</p>" +

                            "</body>" +
                            "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send password reset email",
                    e
            );
        }
    }

    public void resetPassword(
            String token,
            String newPassword,
            String confirmPassword) {

        // 1. Find token
        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid reset token"
                                ));

        // 2. Check whether token was already used
        if (resetToken.isUsed()) {

            throw new IllegalArgumentException(
                    "Reset token has already been used"
            );
        }

        // 3. Check expiry
        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Reset token has expired"
            );
        }

        // 4. Check passwords
        if (!newPassword.equals(confirmPassword)) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        // 5. Get user
        User user = resetToken.getUser();

        // 6. Encode new password
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        // 7. Save updated user
        userRepository.save(user);

        // 8. Mark token as used
        resetToken.setUsed(true);

        passwordResetTokenRepository.save(resetToken);
    }
}