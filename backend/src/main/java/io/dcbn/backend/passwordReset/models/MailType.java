package io.dcbn.backend.passwordReset.models;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Calendar;
import java.util.EnumSet;
import javax.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public enum MailType {

  PASSWORD_RESET {
    @Override
    public String generateMailBody(DcbnUser user) {
      Calendar now = Calendar.getInstance();
      now.add(Calendar.MINUTE, resetTokenDurationInMinutes);

      return Jwts.builder()
          .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
          .setSubject("" + user.getId())
          .setExpiration(now.getTime())
          .compact();
    }
  };

  @Component
  public static class ReportTypeServiceInjector {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jtw.reset.duration}")
    private int resetTokenDurationInMinutes;

    @PostConstruct
    public void postConstruct() {
      for (MailType mt : EnumSet.allOf(MailType.class)) {
        mt.setSecret(secret);
        mt.setResetTokenDurationInMinutes(resetTokenDurationInMinutes);
      }
    }
  }

  @Setter
  protected String secret;

  @Setter
  protected int resetTokenDurationInMinutes = 30;

  public abstract String generateMailBody(DcbnUser user);
}