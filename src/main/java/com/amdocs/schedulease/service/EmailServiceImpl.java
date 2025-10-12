package com.amdocs.schedulease.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.amdocs.schedulease.entity.Booking;

import java.time.format.DateTimeFormatter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;  // ADD THIS IMPORT
import org.slf4j.LoggerFactory;  // ADD THIS IMPORT
@Service
public class EmailServiceImpl implements EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender mailSender;
    

    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    @Override
    public void sendBookingCancellationEmail(Booking booking, String cancelReason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Cancelled - SchedulEase");
            helper.setFrom("harshsinha190@gmail.com");
            
            String emailContent = buildCancellationEmailContent(booking, cancelReason);
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("✅ Cancellation email sent to: " + booking.getUser().getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send cancellation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendBookingApprovalEmail(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Approved ✅ - SchedulEase");
            helper.setFrom("harshsinha190@gmail.com");
            
            String emailContent = buildApprovalEmailContent(booking);
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("✅ Approval email sent to: " + booking.getUser().getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send approval email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendBookingDeclinedEmail(Booking booking, String declineReason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Declined - SchedulEase");
            helper.setFrom("harshsinha190@gmail.com");
            
            String emailContent = buildDeclinedEmailContent(booking, declineReason);
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("✅ Declined email sent to: " + booking.getUser().getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send declined email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildCancellationEmailContent(Booking booking, String cancelReason) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "    <style>" +
            "        body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f4f4; padding: 20px; margin: 0; }" +
            "        .container { background-color: white; padding: 40px; border-radius: 10px; max-width: 600px; margin: 0 auto; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
            "        .header { background: linear-gradient(135deg, #dc3545 0%, #c82333 100%); color: white; padding: 30px; border-radius: 8px; text-align: center; margin-bottom: 30px; }" +
            "        .header h1 { margin: 0; font-size: 28px; }" +
            "        .content { padding: 0 20px; }" +
            "        .booking-details { background-color: #f8f9fa; padding: 20px; border-left: 4px solid #dc3545; margin: 25px 0; border-radius: 5px; }" +
            "        .booking-details h3 { margin-top: 0; color: #333; }" +
            "        .booking-details p { margin: 10px 0; color: #555; }" +
            "        .reason-box { background-color: #fff3cd; padding: 20px; border-left: 4px solid #ffc107; margin: 25px 0; border-radius: 5px; }" +
            "        .reason-box h3 { margin-top: 0; color: #856404; }" +
            "        .footer { text-align: center; color: #999; font-size: 13px; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; }" +
            "        .highlight { color: #dc3545; font-weight: bold; }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "    <div class='container'>" +
            "        <div class='header'>" +
            "            <h1>🚫 Booking Cancelled</h1>" +
            "        </div>" +
            "        <div class='content'>" +
            "            <p>Dear <strong>" + booking.getUser().getFullName() + "</strong>,</p>" +
            "            <p>We regret to inform you that your booking has been <span class='highlight'>cancelled</span> by our staff.</p>" +
            "            <div class='booking-details'>" +
            "                <h3>📋 Booking Details</h3>" +
            "                <p><strong>Booking ID:</strong> #" + booking.getId() + "</p>" +
            "                <p><strong>Start Time:</strong> " + booking.getStartDatetime().format(DATE_FORMATTER) + "</p>" +
            "                <p><strong>End Time:</strong> " + booking.getEndDatetime().format(DATE_FORMATTER) + "</p>" +
            "                <p><strong>Capacity:</strong> " + booking.getTotalCapacityRequested() + " people</p>" +
            "            </div>" +
            "            <div class='reason-box'>" +
            "                <h3>📝 Cancellation Reason</h3>" +
            "                <p>" + cancelReason + "</p>" +
            "            </div>" +
            "            <p>We sincerely apologize for any inconvenience this may cause. If you have any questions or would like to reschedule, please don't hesitate to contact us.</p>" +
            "            <p style='margin-top: 30px;'>Best regards,<br><strong>SchedulEase Team</strong></p>" +
            "        </div>" +
            "        <div class='footer'>" +
            "            <p>&copy; 2025 SchedulEase by Amdocs. All rights reserved.</p>" +
            "            <p>This is an automated email. Please do not reply directly to this message.</p>" +
            "        </div>" +
            "    </div>" +
            "</body>" +
            "</html>";
    }

    private String buildApprovalEmailContent(Booking booking) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "    <style>" +
            "        body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f4f4; padding: 20px; margin: 0; }" +
            "        .container { background-color: white; padding: 40px; border-radius: 10px; max-width: 600px; margin: 0 auto; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
            "        .header { background: linear-gradient(135deg, #28a745 0%, #218838 100%); color: white; padding: 30px; border-radius: 8px; text-align: center; margin-bottom: 30px; }" +
            "        .header h1 { margin: 0; font-size: 28px; }" +
            "        .content { padding: 0 20px; }" +
            "        .booking-details { background-color: #d4edda; padding: 20px; border-left: 4px solid #28a745; margin: 25px 0; border-radius: 5px; }" +
            "        .booking-details h3 { margin-top: 0; color: #155724; }" +
            "        .booking-details p { margin: 10px 0; color: #155724; }" +
            "        .footer { text-align: center; color: #999; font-size: 13px; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; }" +
            "        .highlight { color: #28a745; font-weight: bold; }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "    <div class='container'>" +
            "        <div class='header'>" +
            "            <h1>✅ Booking Approved!</h1>" +
            "        </div>" +
            "        <div class='content'>" +
            "            <p>Dear <strong>" + booking.getUser().getFullName() + "</strong>,</p>" +
            "            <p>Great news! Your booking request has been <span class='highlight'>approved</span>.</p>" +
            "            <div class='booking-details'>" +
            "                <h3>📋 Confirmed Booking Details</h3>" +
            "                <p><strong>Booking ID:</strong> #" + booking.getId() + "</p>" +
            "                <p><strong>Start Time:</strong> " + booking.getStartDatetime().format(DATE_FORMATTER) + "</p>" +
            "                <p><strong>End Time:</strong> " + booking.getEndDatetime().format(DATE_FORMATTER) + "</p>" +
            "                <p><strong>Capacity:</strong> " + booking.getTotalCapacityRequested() + " people</p>" +
            "            </div>" +
            "            <p>Your booking is now <strong>confirmed</strong> and ready to use. Please arrive on time and ensure all equipment is returned in good condition.</p>" +
            "            <p style='margin-top: 30px;'>We look forward to serving you!<br><strong>SchedulEase Team</strong></p>" +
            "        </div>" +
            "        <div class='footer'>" +
            "            <p>&copy; 2025 SchedulEase by Amdocs. All rights reserved.</p>" +
            "            <p>This is an automated email. Please do not reply directly to this message.</p>" +
            "        </div>" +
            "    </div>" +
            "</body>" +
            "</html>";
    }

    private String buildDeclinedEmailContent(Booking booking, String declineReason) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "    <style>" +
            "        body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f4f4; padding: 20px; margin: 0; }" +
            "        .container { background-color: white; padding: 40px; border-radius: 10px; max-width: 600px; margin: 0 auto; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
            "        .header { background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%); color: #212529; padding: 30px; border-radius: 8px; text-align: center; margin-bottom: 30px; }" +
            "        .header h1 { margin: 0; font-size: 28px; }" +
            "        .content { padding: 0 20px; }" +
            "        .booking-details { background-color: #fff3cd; padding: 20px; border-left: 4px solid #ffc107; margin: 25px 0; border-radius: 5px; }" +
            "        .booking-details h3 { margin-top: 0; color: #856404; }" +
            "        .booking-details p { margin: 10px 0; color: #856404; }" +
            "        .reason-box { background-color: #f8d7da; padding: 20px; border-left: 4px solid #dc3545; margin: 25px 0; border-radius: 5px; }" +
            "        .reason-box h3 { margin-top: 0; color: #721c24; }" +
            "        .footer { text-align: center; color: #999; font-size: 13px; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; }" +
            "        .highlight { color: #ffc107; font-weight: bold; }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "    <div class='container'>" +
            "        <div class='header'>" +
            "            <h1>⚠️ Booking Declined</h1>" +
            "        </div>" +
            "        <div class='content'>" +
            "            <p>Dear <strong>" + booking.getUser().getFullName() + "</strong>,</p>" +
            "            <p>We regret to inform you that your booking request has been <span class='highlight'>declined</span>.</p>" +
            "            <div class='booking-details'>" +
            "                <h3>📋 Requested Booking Details</h3>" +
            "                <p><strong>Booking ID:</strong> #" + booking.getId() + "</p>" +
            "                <p><strong>Requested Start:</strong> " + booking.getStartDatetime().format(DATE_FORMATTER) + "</p>" +
            "                <p><strong>Requested End:</strong> " + booking.getEndDatetime().format(DATE_FORMATTER) + "</p>" +
            "                <p><strong>Capacity:</strong> " + booking.getTotalCapacityRequested() + " people</p>" +
            "            </div>" +
            "            <div class='reason-box'>" +
            "                <h3>📝 Decline Reason</h3>" +
            "                <p>" + declineReason + "</p>" +
            "            </div>" +
            "            <p>Please feel free to submit a new booking request with different dates or contact us for alternative options.</p>" +
            "            <p style='margin-top: 30px;'>Thank you for your understanding,<br><strong>SchedulEase Team</strong></p>" +
            "        </div>" +
            "        <div class='footer'>" +
            "            <p>&copy; 2025 SchedulEase by Amdocs. All rights reserved.</p>" +
            "            <p>This is an automated email. Please do not reply directly to this message.</p>" +
            "        </div>" +
            "    </div>" +
            "</body>" +
            "</html>";
    }
    
    @Override
    public void sendBookingRemindersEmail(String recipientEmail, List<Booking> bookings) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Split comma-separated emails
            String[] emailArray = recipientEmail.split(",");
            
            // Trim whitespace from each email
            for (int i = 0; i < emailArray.length; i++) {
                emailArray[i] = emailArray[i].trim();
            }
            
            helper.setTo(emailArray);
            helper.setSubject("Daily Booking Reminders - " + bookings.size() + " Upcoming Booking(s)");
            helper.setFrom("noreply@schedulease.com");
            
            String htmlContent = buildReminderEmailTemplate(bookings);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Reminder email sent to {} staff member(s)", emailArray.length);
            
        } catch (MessagingException e) {
            logger.error("Failed to send reminder email", e);
            throw new RuntimeException("Failed to send reminder email", e);
        }
    }



    private String buildReminderEmailTemplate(List<Booking> bookings) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<meta charset='UTF-8'>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }")
            .append(".container { max-width: 600px; margin: 20px auto; background: white; }")
            .append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }")
            .append(".content { padding: 30px; }")
            .append(".booking-card { background: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; margin: 15px 0; border-radius: 4px; }")
            .append(".booking-id { color: #667eea; font-weight: bold; font-size: 18px; margin-bottom: 10px; }")
            .append(".label { color: #6c757d; font-size: 12px; text-transform: uppercase; font-weight: bold; margin-top: 10px; }")
            .append(".value { color: #333; font-size: 14px; margin-top: 3px; }")
            .append(".footer { background: #343a40; color: white; padding: 20px; text-align: center; font-size: 12px; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class='container'>")
            .append("<div class='header'>")
            .append("<h1 style='margin: 0;'>📅 Daily Booking Reminders</h1>")
            .append("<p style='margin: 10px 0 0 0;'>Confirmed bookings starting in the next 24 hours</p>")
            .append("</div>")
            .append("<div class='content'>")
            .append("<p>Hello Staff Team,</p>")
            .append("<p>Here are the confirmed bookings scheduled to start within the next 24 hours:</p>");
        
        for (Booking booking : bookings) {
            String roomNames = booking.getRooms().stream()
                .map(r -> r.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");
                
            html.append("<div class='booking-card'>")
                .append("<div class='booking-id'>Booking #").append(booking.getId()).append("</div>")
                .append("<div class='label'>Guest</div>")
                .append("<div class='value'>").append(booking.getUser().getEmail()).append("</div>")
                .append("<div class='label'>Start Date & Time</div>")
                .append("<div class='value'>").append(booking.getStartDatetime().format(dateFormatter))
                .append(" at ").append(booking.getStartDatetime().format(timeFormatter)).append("</div>")
                .append("<div class='label'>End Time</div>")
                .append("<div class='value'>").append(booking.getEndDatetime().format(timeFormatter)).append("</div>")
                .append("<div class='label'>Room(s)</div>")
                .append("<div class='value'>").append(roomNames).append("</div>")
                .append("<div class='label'>Capacity</div>")
                .append("<div class='value'>").append(booking.getTotalCapacityRequested()).append(" people</div>");
            
            if (booking.getBookingReason() != null && !booking.getBookingReason().isEmpty()) {
                html.append("<div class='label'>Purpose</div>")
                    .append("<div class='value'>").append(booking.getBookingReason()).append("</div>");
            }
            
            html.append("</div>");
        }
        
        html.append("<p style='margin-top: 20px;'><strong>Total Bookings:</strong> ").append(bookings.size()).append("</p>")
            .append("<p>Please ensure all rooms and equipment are prepared in advance.</p>")
            .append("</div>")
            .append("<div class='footer'>")
            .append("<p style='margin: 0;'>This is an automated reminder from SchedEase</p>")
            .append("<p style='margin: 5px 0 0 0;'>© 2025 SchedEase. All rights reserved.</p>")
            .append("</div>")
            .append("</div>")
            .append("</body>")
            .append("</html>");
        
        return html.toString();
    }
    
    
}
