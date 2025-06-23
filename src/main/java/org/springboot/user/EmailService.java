 package org.springboot.user;


import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) throws UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject("Your OTP for Verification");
            helper.setFrom("nadezhdaventur@gmail.com", "Nadezhda Venture");

            String htmlContent = """
                <html>
                  <body style="font-family: Arial, sans-serif; font-size: 14px;">
                    <h2 style="color: #ff6600;">Your OTP Code</h2>
                    <p>Your One-Time Password (OTP) is:</p>
                    <h1 style="color: #333; background-color: #f2f2f2; padding: 10px; display: inline-block;">%s</h1>
                    <p>This OTP is valid for <strong>5 minutes</strong>. Do not share it with anyone.</p>
                    <br>
                    <p>If you did not request this OTP, please ignore this email.</p>
                    <br>
                    <p>Regards,<br><strong>Nadezhda Security Team</strong></p>
                  </body>
                </html>
                """.formatted(otp);

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("OTP email sent to {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}", toEmail, e);
        }
    }

    public void sendSuccessEmail(String toEmail) throws UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject("Registration Successful");
            helper.setFrom("nadezhdaventur@gmail.com", "Nadezhda Venture");

            String htmlContent = """
                <html>
                  <body style="font-family: Arial, sans-serif; font-size: 14px;">
                    <h2 style="color: #28a745;">Congratulations!</h2>
                    <p>You have successfully registered with <strong>Nadezhda Venture</strong>.</p>
                    <p>We're excited to have you onboard.</p>
                    <br>
                    <p>Regards,<br><strong>Nadezhda Exams Team</strong></p>
                  </body>
                </html>
                """;

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("Success email sent to {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send success email to {}", toEmail, e);
        }
    }
    
    public void sendRegistrationConfirmation(String toEmail, String userName, String registrationId) throws UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject("Your Registration is Confirmed");

            // Custom from address and name
            helper.setFrom("nadezhdaventur@gmail.com", "Nadezhda Venture");

            // Use Text Blocks for HTML body
            String htmlContent = """
                <html>
                  <body style="font-family: Arial, sans-serif; font-size: 14px;">
                    <h2 style="color: #2d89ef;">Dear %s,</h2>
                    <p>Thank you for registering with <strong>Nadezhda Venture</strong>.</p>
                    <p>Your <b>Registration ID</b> is: <strong style="color: green;">%s</strong></p>
                    <p>We look forward to seeing you succeed!</p>
                    <br>
                    <p>Regards,<br><strong>Nadezhda Exams Team</strong></p>
                  </body>
                </html>
                """.formatted(userName, registrationId);

            helper.setText(htmlContent, true); // true = send as HTML
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace(); // Or use proper logging
        }
    }
    
    public void sendProfileUpdateConfirmation(String toEmail, String userName) throws UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject("Your Profile Has Been Updated Successfully"); // Updated subject

            // Custom from address and name (same as registration email)
            helper.setFrom("nadezhdaventur@gmail.com", "Nadezhda Venture");

            // HTML body for profile update confirmation
            String htmlContent = """
                <html>
                  <body style="font-family: Arial, sans-serif; font-size: 14px;">
                    <h2 style="color: #2d89ef;">Dear %s,</h2>
                    <p>This is to confirm that your profile on <strong>Nadezhda Venture</strong> has been successfully updated.</p>
                    <p>If you did not make these changes, please contact us immediately.</p>
                    <br>
                    <p>Regards,<br><strong>Nadezhda Exams Team</strong></p>
                  </body>
                </html>
                """.formatted(userName); // Only userName is needed here

            helper.setText(htmlContent, true); // true = send as HTML
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace(); // Or use proper logging
        }
    }

	public void sendExamNotification(String email, String title, String description, LocalDateTime scheduledDateTime) {
		// TODO Auto-generated method stub
		
	}

//	public void sendEmail(String email, String subject, String body) {
//		// TODO Auto-generated method stub
//		
//	}
	
	
    public void sendProfileUpdateNotification(String toEmail, String userName, String registrationId, Map<String, String> changes) throws UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject("Your Profile Has Been Updated - Registration ID: " + registrationId);
            helper.setFrom("nadezhdaventur@gmail.com", "Nadezhda Venture");

            StringBuilder changesHtml = new StringBuilder();
            if (changes.isEmpty()) {
                changesHtml.append("<p>No significant changes were detected.</p>");
            } else {
                changesHtml.append("<p>The following changes have been made to your profile:</p>");
                changesHtml.append("<ul style=\"list-style-type: none; padding: 0;\">");
                changes.forEach((field, detail) ->
                    changesHtml.append(String.format("<li><strong>%s:</strong> %s</li>", field, detail))
                );
                changesHtml.append("</ul>");
            }

            String htmlContent = """
                <html>
                  <body style="font-family: Arial, sans-serif; font-size: 14px;">
                    <h2 style="color: #2d89ef;">Dear %s,</h2>
                    <p>This is to confirm that your profile associated with Registration ID: <strong style="color: green;">%s</strong> has been updated.</p>
                    %s
                    <p>If you did not make these changes, please contact us immediately.</p>
                    <br>
                    <p>Regards,<br><strong>Nadezhda Exams Team</strong></p>
                  </body>
                </html>
                """.formatted(userName, registrationId, changesHtml.toString());

            helper.setText(htmlContent, true); // true = send as HTML
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            logger.error("Failed to send profile update notification email to {}: {}", toEmail, e.getMessage());
            // Log the exception, but don't rethrow if email is not critical for main operation
        }
    }
}
