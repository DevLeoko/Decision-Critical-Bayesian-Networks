package io.dcbn.backend;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.models.Role;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.authentication.services.DcbnUserDetailsService;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.NodeDependency;
import io.dcbn.backend.graph.Position;
import io.dcbn.backend.graph.StateType;
import io.dcbn.backend.graph.repositories.GraphRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import io.dcbn.backend.evidenceFormula.services.visitors.FunctionWrapper;
import java.util.HashMap;
import java.util.Map;
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

      NodeDependency nodeATimeZeroDependency = new NodeDependency(0, new ArrayList<>(),
          new ArrayList<>(), new double[][]{{0.3, 0.7}});
      NodeDependency nodeATimeTDependency = new NodeDependency(0, new ArrayList<>(),
          new ArrayList<>(), new double[][]{{0.5, 0.5}});
      Node a = new Node(0, "A", nodeATimeZeroDependency, nodeATimeTDependency, null, null,
          StateType.BOOLEAN,
          ZERO_POSITION);
      NodeDependency nodeBTimeZeroDependency = new NodeDependency(0, new ArrayList<>(),
          new ArrayList<>(), new double[][]{{0.2, 0.8}});
      NodeDependency nodeBTimeTDependency = new NodeDependency(0, new ArrayList<>(),
          new ArrayList<>(), new double[][]{{0.5, 0.5}});
      Node b = new Node(0, "B", nodeBTimeZeroDependency, nodeBTimeTDependency, null, null,
          StateType.BOOLEAN,
          ZERO_POSITION);

      NodeDependency nodeCTimeZeroDependency = new NodeDependency(0, Arrays.asList(a, b),
          new ArrayList<>(),
          new double[][]{{0.999, 0.001}, {0.6, 0.4}, {0.8, 0.2}, {0.2, 0.8}});

      Node c = new Node(0, "C", nodeCTimeZeroDependency, null, null, null, StateType.BOOLEAN,
          ZERO_POSITION);
      NodeDependency nodeCTimeTDependency = new NodeDependency(0, Arrays.asList(a, b),
          Collections.singletonList(c), new double[][]{{0.1, 0.9},
          {0.2, 0.8}, {0.3, 0.7}, {0.4, 0.6}, {0.5, 0.5}, {0.6, 0.4}, {0.7, 0.3}, {0.8, 0.2}});
      c.setTimeTDependency(nodeCTimeTDependency);
      List<Node> nodeList = Arrays.asList(a, b, c);
      graphRepository.save(new Graph(0, "testGraph", 10, nodeList));
    };
  }

  @Bean
  public Map<String, FunctionWrapper> variablesBean() {
    return new HashMap<>();
  }

}
