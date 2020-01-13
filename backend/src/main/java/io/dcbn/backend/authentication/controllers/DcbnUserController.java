package io.dcbn.backend.authentication.controllers;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.passwordReset.models.MailType;
import io.dcbn.backend.passwordReset.services.MailService;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DcbnUserController {

  private final MailService mailService;
  private final DcbnUserRepository dcbnUserRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public DcbnUserController(MailService mailService,
      DcbnUserRepository dcbnUserRepository,
      PasswordEncoder passwordEncoder) {
    this.mailService = mailService;
    this.dcbnUserRepository = dcbnUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/users")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public Iterable<DcbnUser> getUsers() {
    return dcbnUserRepository.findAll();
  }

  @GetMapping("/users/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<DcbnUser> getUserById(@PathVariable long id) {
    Optional<DcbnUser> user = dcbnUserRepository.findById(id);
    return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  private String generatePassword() {
    return "TEST";
  }

  @PostMapping("/users")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<Void> createUser(@Valid @RequestBody DcbnUser user) {
    if (dcbnUserRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
      return ResponseEntity.badRequest().build();
    }

    String password = generatePassword();
    user.setPassword(password);

    mailService.sendMail(user.getEmail(), MailType.PASSWORD_RESET);

    dcbnUserRepository.save(user.withEncryptedPassword(passwordEncoder));
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<Void> editUser(@PathVariable long id, @Valid @RequestBody DcbnUser user) {
    Optional<DcbnUser> optional = dcbnUserRepository.findById(id);
    if (!optional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    DcbnUser oldUser = optional.get();

    oldUser.setUsername(user.getUsername());
    oldUser.setEmail(user.getEmail());
    oldUser.setRole(user.getRole());
    dcbnUserRepository.save(oldUser);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<Void> deleteUserById(@PathVariable long id) {
    if (!dcbnUserRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    dcbnUserRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
