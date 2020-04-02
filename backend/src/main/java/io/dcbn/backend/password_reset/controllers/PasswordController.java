package io.dcbn.backend.password_reset.controllers;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.password_reset.models.MailType;
import io.dcbn.backend.password_reset.models.ResetRequest;
import io.dcbn.backend.password_reset.services.MailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class PasswordController {

    private final MailService mailService;
    private final DcbnUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    public PasswordController(MailService mailService,
                              DcbnUserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/request-password")
    public void requestPassword(@RequestBody String email) {
        DcbnUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mailService.sendMail(user, MailType.PASSWORD_RESET);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetRequest resetRequest) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .parseClaimsJws(resetRequest.getToken())
                    .getBody();
            long id = Long.parseLong(claims.getSubject());

            DcbnUser user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND));

            user.setPassword(passwordEncoder.encode(resetRequest.getPassword()));
            userRepository.save(user);

        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
