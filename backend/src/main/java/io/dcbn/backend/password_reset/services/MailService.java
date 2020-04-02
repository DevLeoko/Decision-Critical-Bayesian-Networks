package io.dcbn.backend.password_reset.services;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.password_reset.models.MailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    private final JavaMailSender sender;

    @Value("${reset.address}")
    private String fromAddress;

    @Autowired
    public MailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void sendMail(DcbnUser user, MailType type) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(fromAddress);
            helper.setTo(user.getEmail());
            helper.setText(type.generateMailBody(user), true);

            sender.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}