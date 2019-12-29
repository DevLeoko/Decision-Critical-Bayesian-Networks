package io.dcbn.backend;

import io.dcbn.backend.login.models.DcbnUser;
import io.dcbn.backend.login.models.Role;
import io.dcbn.backend.login.repositories.DcbnUserRepository;
import io.dcbn.backend.login.services.DcbnUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DcbnApplication {

  public static void main(String[] args) {
    SpringApplication.run(DcbnApplication.class, args);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(DcbnUserRepository dcbnUserRepository) {
    return new DcbnUserDetailsService(dcbnUserRepository);
  }

  @Bean
  public CommandLineRunner commandLineRunner(DcbnUserRepository dcbnUserRepository) {
    return args -> {
      dcbnUserRepository.save(
          new DcbnUser("admin", "admin@dcbn.io", passwordEncoder().encode("admin"), Role.ADMIN));
      dcbnUserRepository.save(
          new DcbnUser("moderator", "moderator@dcbn.io", passwordEncoder().encode("moderator"),
              Role.MODERATOR));
      dcbnUserRepository.save(
          new DcbnUser("superadmin", "superadmin@dcbn.io", passwordEncoder().encode("superadmin"),
              Role.SUPERADMIN));
    };
  }

}
