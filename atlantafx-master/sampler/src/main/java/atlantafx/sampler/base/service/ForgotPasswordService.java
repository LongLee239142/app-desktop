package atlantafx.sampler.base.service;

import atlantafx.sampler.base.configJDBC.dao.JDBCConnect;
import atlantafx.sampler.base.util.PasswordUtils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ForgotPasswordService {

    // Method to handle the forgot password functionality
    public static void handleForgotPassword(String role, String username) {
        // Generate a new random password
        String newPassword = generateRandomPassword();

        // Hash the password using BCrypt
        String hashedPassword = PasswordUtils.hashPassword(newPassword);

        // Update the password in the database
        if ("Admin".equals(role)) {
            updateAdminPassword(username, hashedPassword);
        } else if ("Staff".equals(role)) {
            updateStaffPassword(username, hashedPassword);
        }

        // Send the new password to the user's email
        sendPasswordResetEmail(role, username, newPassword);

        // Notify user of success
        System.out.println("A new password has been sent to your email.");
    }

    // Generate a random password consisting of 6 digits
    private static String generateRandomPassword() {
        int length = 6;
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append((int)(Math.random() * 10)); // Append a random digit
        }
        return password.toString();
    }

    // Update the admin password in the database
    private static void updateAdminPassword(String username, String hashedPassword) {
        String updateQuery = "UPDATE admin SET password_hash = ? WHERE username = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update the staff password in the database
    private static void updateStaffPassword(String staffId, String hashedPassword) {
        String updateQuery = "UPDATE staff SET password_hash = ? WHERE staff_id = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, staffId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Send the password reset email
    private static void sendPasswordResetEmail(String role, String username, String newPassword) {
        String recipientEmail = getUserEmail(role, username);
        String subject = "Password Reset";
        String content = "Your new password is: " + newPassword;

        try {
            // Gmail SMTP server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");  // TLS port
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");  // Enable STARTTLS for encryption

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("leminhduc1212001@gmail.com", "txsp ycqq omhb ochp ");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("leminhduc1212001@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Get user email based on their role and username
    private static String getUserEmail(String role, String username) {
        String email = null;
        if ("Admin".equals(role)) {
            email = getAdminEmail(username);
        } else if ("Staff".equals(role)) {
            email = getStaffEmail(username);
        }
        return email;
    }

    // Query database to get admin email based on username
    private static String getAdminEmail(String username) {
        String email = null;
        String query = "SELECT email FROM admin WHERE username = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    // Query database to get staff email based on staffId
    private static String getStaffEmail(String staffId) {
        String email = null;
        String query = "SELECT email FROM staff WHERE staff_id = ?";
        try (Connection conn = JDBCConnect.getJDBCConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }
}
