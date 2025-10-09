package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

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
}
