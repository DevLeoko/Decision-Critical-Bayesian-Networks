package io.dcbn.backend.authentication.repositories;

import io.dcbn.backend.authentication.models.DcbnUser;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DcbnUserRepository extends CrudRepository<DcbnUser, Long> {

  Optional<DcbnUser> findByUsernameOrEmail(String username, String email);

  boolean existsByUsernameOrEmail(String username, String email);

}
