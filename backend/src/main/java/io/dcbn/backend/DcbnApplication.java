package io.dcbn.backend;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.models.Role;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.authentication.services.DcbnUserDetailsService;
import io.dcbn.backend.core.AoiCache;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.evidence_formula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.evidence_formula.services.DefaultFunctionProvider;
import io.dcbn.backend.evidence_formula.services.FunctionProvider;
import io.dcbn.backend.graph.*;
import io.dcbn.backend.graph.repositories.GraphRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableScheduling
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
    public MailSender mailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public CommandLineRunner commandLineRunner(DcbnUserRepository dcbnUserRepository,
                                               GraphRepository graphRepository,
                                               EvidenceFormulaRepository evidenceFormulaRepository) {
        return args -> {
            dcbnUserRepository.save(
                    new DcbnUser("admin", "admin@dcbn.io", passwordEncoder().encode("admin"), Role.ADMIN));
            dcbnUserRepository.save(
                    new DcbnUser("moderator", "moderator@dcbn.io", passwordEncoder().encode("moderator"),
                            Role.MODERATOR));
            dcbnUserRepository.save(
                    new DcbnUser("superadmin", "superadmin@dcbn.io", passwordEncoder().encode("superadmin"),
                            Role.SUPERADMIN));

            dcbnUserRepository.save(new DcbnUser("a", "a@a.de", passwordEncoder().encode("a"), Role.ADMIN));

            Node smuggling = new Node("smuggling", null, null, "#ff00ff", null, StateType.BOOLEAN,
                    new Position(0.0, 0.0));
            Node nullSpeed = new Node("nullSpeed", null, null, "#ff9900",
                    null, StateType.BOOLEAN, new Position(200.0, 0.0));
            Node inTrajectoryArea = new Node("inTrajectoryArea", null, null, "#00ffff",
                    null, StateType.BOOLEAN, new Position(0.0, 200.0));
            Node isInReportedArea = new Node("isInReportedArea", null, null, "#ffff00",
                    null, StateType.BOOLEAN, new Position(-200.0, 0.0));

            List<Node> smugglingParentsList = Arrays
                    .asList(nullSpeed, inTrajectoryArea, isInReportedArea);
            double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}, {0.4, 0.6}, {0.4, 0.6}, {0.2, 0.8},
                    {0.2, 0.8}, {0.001, 0.999}, {0.001, 0.999}};
            NodeDependency smuggling0Dep = new NodeDependency(smugglingParentsList,
                    new ArrayList<>(), probabilities);
            NodeDependency smugglingTDep = new NodeDependency(smugglingParentsList, Collections.emptyList(),
                    probabilities);
            smuggling.setTimeZeroDependency(smuggling0Dep);
            smuggling.setTimeTDependency(smugglingTDep);

            NodeDependency nS0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.7, 0.3}});
            NodeDependency nSTDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.7, 0.3}});
            nullSpeed.setTimeZeroDependency(nS0Dep);
            nullSpeed.setTimeTDependency(nSTDep);

            NodeDependency iTA0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            NodeDependency iTATDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            inTrajectoryArea.setTimeZeroDependency(iTA0Dep);
            inTrajectoryArea.setTimeTDependency(iTATDep);

            NodeDependency iIRA0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            NodeDependency iIRATDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            isInReportedArea.setTimeZeroDependency(iIRA0Dep);
            isInReportedArea.setTimeTDependency(iIRATDep);
            graphRepository.save(new Graph(0, "testGraph", 5,
                    Arrays.asList(nullSpeed, inTrajectoryArea, isInReportedArea, smuggling)));
        };
    }

    @Bean
    public FunctionProvider functionsBean(VesselCache vesselCache, AoiCache aoiCache) {
        return new DefaultFunctionProvider(vesselCache, aoiCache);
    }

}
