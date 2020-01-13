package io.dcbn.backend.passwordReset.services;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.passwordReset.models.MailType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MailService {

  private static final int TOKEN_DURATION_MINUTES = 30;

  private final MailSender sender;
  private final DcbnUserRepository userRepository;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${reset.address}")
  private String fromAddress;

  @Autowired
  public MailService(MailSender sender,
      DcbnUserRepository userRepository) {
    this.sender = sender;
    this.userRepository = userRepository;
  }

  public void sendMail(String email, MailType type) throws MailException {
    if (type == MailType.PASSWORD_RESET) { // TODO: Irgendeine Idee wie man das ohne if-Abfragen loesen kann?
      DcbnUser user = userRepository.findByUsernameOrEmail("", email)
          .orElseThrow(() -> new ResponseStatusException(
              HttpStatus.NOT_FOUND));

      Calendar now = Calendar.getInstance();
      now.add(Calendar.MINUTE, TOKEN_DURATION_MINUTES);

      String token = Jwts.builder()
          .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
          .setSubject("" + user.getId())
          .setExpiration(now.getTime())
          .compact();

      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromAddress);
      message.setTo(email);
      message.setText(token); // TODO: Emails schreiben.

      sender.send(message);
    }
  }

}
