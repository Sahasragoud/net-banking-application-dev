package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.service.KycEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class KycEmailServiceImpl implements KycEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendKycApproved(String custName, String empName, String email, String subject) {
        try{
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(
                    """
                    Dear %s,
                    Our team has considered you KYC application and validated it. We are happy to say that your Registration for OptimaNet Bank is completed successfully.
                    
                    Thanks Regards,
                    %s,
                    OptimaNet Bank.
                    """.formatted(custName, empName)
            );

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendKycRejected(String custName, String empName, String email, String subject, String reason) {
        try{
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText("""
                    Dear %s,
                    
                    We had considered your Kyc application for registering into OptimaNet Bank.Unfortunately we cannot process you registration any further because %s.
                    
                    Thanks regards,
                    %s,
                    OptimaNet Bank.
                    """.formatted(custName, reason, empName)
            );

            mailSender.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendKycCreated(String custName, String email, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom("no-reply@optimanetbank.com");

            helper.setText(
                    """
                    Dear %s,
    
                    Thank you for registering with OptimaNet Bank.
    
                    We are pleased to inform you that your KYC application has been successfully created.
    
                    Please proceed to upload all required documents clearly and accurately. Once all documents are submitted, our verification team will initiate the review process.
    
                    Current Status: KYC Application Created
                    Next Step: Document Upload In Progress
    
                    You will be notified as soon as the review process begins.
    
                    If you did not initiate this request or believe this action was taken in error, please contact our support team immediately.
    
                    Warm regards,
                    OptimaNet Bank
                    Customer Verification Team
                    """.formatted(custName)
            );

            mailSender.send(message);

        } catch (MessagingException ex) {
            throw new IllegalStateException(
                    "Failed to send KYC creation email to " + email,
                    ex
            );
        }
    }

    @Override
    public void sendKycAssigned(String custName, String email, String subject) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(
                    """
                            Dear %s,
                            
                            THis is an update regarding your KYC application with OptimaNet Bank.
                                                                
                            We have successfully received your application and the associated documents. \s
                            Our verification team is reviewing the details and may contact you if additional information or clarification is required.
                            
                            Current Status: KYC Application IS UNDER REVIEW \s
                            Next Step: Verification in progress
                            
                            You will be notified once the review process is completed.
                            
                            If you did not initiate this request or believe this submission was made in error, please contact our support team immediately.
                            
                            Warm regards, \s
                            OptimaNet Bank \s
                            Customer Verification Team
                    """.formatted(custName)
            );
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
