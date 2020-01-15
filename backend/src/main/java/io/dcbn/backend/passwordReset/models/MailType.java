package io.dcbn.backend.passwordReset.models;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Calendar;
import org.springframework.stereotype.Component;

@Component
public enum MailType {
  PASSWORD_RESET {
    @Override
    public String generateMailBody(DcbnUser user, String secret) {
      Calendar now = Calendar.getInstance();
      now.add(Calendar.MINUTE, TOKEN_DURATION_MINUTES);

      return Jwts.builder()
          .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
          .setSubject("" + user.getId())
          .setExpiration(now.getTime())
          .compact();
    }
  };

  private static final int TOKEN_DURATION_MINUTES = 30;

  public abstract String generateMailBody(DcbnUser user, String secret);
}
