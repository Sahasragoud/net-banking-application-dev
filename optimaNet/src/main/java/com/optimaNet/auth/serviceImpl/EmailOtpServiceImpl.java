package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailOtpServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String toEmail,String subject, String otp) {

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText("""
                Dear User,
                   
                    Your OTP for OptimaNet registration is: %s
                   
                    This OTP is valid for 5 minutes.
                   
                    — OptimaNet Security Team
                """.formatted(otp)
            );
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
