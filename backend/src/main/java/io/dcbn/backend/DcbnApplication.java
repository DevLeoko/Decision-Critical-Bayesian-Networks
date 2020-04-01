package io.dcbn.backend;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.models.Role;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.authentication.services.DcbnUserDetailsService;
import io.dcbn.backend.core.AoiCache;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.evidence_formula.services.DefaultFunctionProvider;
import io.dcbn.backend.evidence_formula.services.FunctionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class DcbnApplication {

    @Value("${superadmin.email}")
    private String superadminEmail;

    @Value("${superadmin.password}")
    private String superadminPassword;

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
                    new DcbnUser("superadmin", superadminEmail, passwordEncoder().encode(superadminPassword),
                            Role.SUPERADMIN));
        };
    }

    @Bean
    public FunctionProvider functionsBean(VesselCache vesselCache, AoiCache aoiCache) {
        return new DefaultFunctionProvider(vesselCache, aoiCache);
    }

}
