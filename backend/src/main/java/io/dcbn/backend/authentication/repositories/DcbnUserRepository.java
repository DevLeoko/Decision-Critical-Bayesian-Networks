package io.dcbn.backend.authentication.repositories;

import io.dcbn.backend.authentication.models.DcbnUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DcbnUserRepository extends CrudRepository<DcbnUser, Long> {

    Optional<DcbnUser> findByUsernameOrEmail(String username, String email);

    Optional<DcbnUser> findByUsername(String username);

    Optional<DcbnUser> findByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

}
