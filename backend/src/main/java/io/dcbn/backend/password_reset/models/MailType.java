package io.dcbn.backend.password_reset.models;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.EnumSet;

@Component
public enum MailType {

    PASSWORD_RESET {
        @Override
        public String generateMailBody(DcbnUser user) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, resetTokenDurationInMinutes);

            String token = Jwts.builder()
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
                    .setSubject("" + user.getId())
                    .setExpiration(now.getTime())
                    .compact();

            return passwordResetTemplate.replace("{key}", token);
        }
    };

    @Component
    public static class MailTypeInjector {
        @Value("${jwt.secret}")
        private String secret;

        @Value("${jtw.reset.duration}")
        private int resetTokenDurationInMinutes;

        @Value("${reset.template}")
        private String resetPasswordTemplateFilename;

        @PostConstruct
        public void postConstruct() throws URISyntaxException, IOException {
            Path path = Paths.get("src", "main", "resources", resetPasswordTemplateFilename);
            byte[] bytes = Files.readAllBytes(path);
            String passwordResetTemplate = new String(bytes, StandardCharsets.UTF_8);
            for (MailType mt : EnumSet.allOf(MailType.class)) {
                mt.setSecret(secret);
                mt.setResetTokenDurationInMinutes(resetTokenDurationInMinutes);
                mt.setPasswordResetTemplate(passwordResetTemplate);
            }
        }
    }

    @Setter
    protected String secret;

    @Setter
    protected int resetTokenDurationInMinutes = 30;

    @Setter
    protected String passwordResetTemplate;

    public abstract String generateMailBody(DcbnUser user);
}
