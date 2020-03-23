package io.dcbn.backend.password_reset.services;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.password_reset.models.MailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final MailSender sender;

    @Value("${reset.address}")
    private String fromAddress;

    @Autowired
    public MailService(MailSender sender) {
        this.sender = sender;
    }

    public void sendMail(DcbnUser user, MailType type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setText(type.generateMailBody(user));

        sender.send(message);
    }
}