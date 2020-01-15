package io.dcbn.backend.passwordReset.services;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.passwordReset.models.MailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  private final MailSender sender;

  @Value("${reset.address}")
  private String fromAddress;

  @Value("${jwt.secret}")
  private String secret;

  @Autowired
  public MailService(MailSender sender) {
    this.sender = sender;
  }

  public void sendMail(DcbnUser user, MailType type) throws MailException {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);
    message.setTo(user.getEmail());
    message.setText(type.generateMailBody(user, secret));

    sender.send(message);
  }
}