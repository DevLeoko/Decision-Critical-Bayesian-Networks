package io.dcbn.backend;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.models.Role;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.authentication.services.DcbnUserDetailsService;
import io.dcbn.backend.evidenceFormula.services.DefaultFunctionProvider;
import io.dcbn.backend.evidenceFormula.services.FunctionProvider;
import io.dcbn.backend.graph.*;
import io.dcbn.backend.graph.repositories.GraphRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public MailSender mailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public CommandLineRunner commandLineRunner(DcbnUserRepository dcbnUserRepository,
                                               GraphRepository graphRepository) {
        return args -> {
            dcbnUserRepository.save(
                    new DcbnUser("admin", "admin@dcbn.io", passwordEncoder().encode("admin"), Role.ADMIN));
            dcbnUserRepository.save(
                    new DcbnUser("moderator", "moderator@dcbn.io", passwordEncoder().encode("moderator"),
                            Role.MODERATOR));
            dcbnUserRepository.save(
                    new DcbnUser("superadmin", "superadmin@dcbn.io", passwordEncoder().encode("superadmin"),
                            Role.SUPERADMIN));

            Position ZERO_POSITION = new Position(0.0, 0.0);

            Node smuggling = new Node(0, "smuggling", null, null, "#ffffff", null, StateType.BOOLEAN,
                    ZERO_POSITION);
            Node nullSpeed = new Node(0, "nullSpeed", null, null, "#ffffff",
                    "nullSpeed", StateType.BOOLEAN, ZERO_POSITION);
            Node inTrajectoryArea = new Node(0, "inTrajectoryArea", null, null, "#ffffff",
                    "inTrajectory", StateType.BOOLEAN, ZERO_POSITION);
            Node isInReportedArea = new Node(0, "isInReportedArea", null, null, "#ffffff",
                    "inArea", StateType.BOOLEAN, ZERO_POSITION);

            List<Node> smugglingParentsList = Arrays
                    .asList(isInReportedArea, inTrajectoryArea, nullSpeed);
            double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}, {0.4, 0.6}, {0.4, 0.6}, {0.2, 0.8},
                    {0.2, 0.8}, {0.001, 0.999}, {0.001, 0.999}};
            NodeDependency smuggling0Dep = new NodeDependency(0, smugglingParentsList,
                    new ArrayList<>(), probabilities);
            NodeDependency smugglingTDep = new NodeDependency(0, smugglingParentsList, new ArrayList<>(),
                    probabilities);
            smuggling.setTimeZeroDependency(smuggling0Dep);
            smuggling.setTimeTDependency(smugglingTDep);

            NodeDependency nS0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.7, 0.3}});
            NodeDependency nSTDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.7, 0.3}});
            nullSpeed.setTimeZeroDependency(nS0Dep);
            nullSpeed.setTimeTDependency(nSTDep);

            NodeDependency iTA0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            NodeDependency iTATDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            inTrajectoryArea.setTimeZeroDependency(iTA0Dep);
            inTrajectoryArea.setTimeTDependency(iTATDep);

            NodeDependency iIRA0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            NodeDependency iIRATDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
                    new double[][]{{0.8, 0.2}});
            isInReportedArea.setTimeZeroDependency(iIRA0Dep);
            isInReportedArea.setTimeTDependency(iIRATDep);
            graphRepository.save(new Graph(0, "testGraph", 5,
                    Arrays.asList(nullSpeed, inTrajectoryArea, isInReportedArea, smuggling)));
        };
    }

    @Bean
    public FunctionProvider functionsBean() {
        return new DefaultFunctionProvider();
    }

}
