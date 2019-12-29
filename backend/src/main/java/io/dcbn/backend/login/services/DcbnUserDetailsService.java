package io.dcbn.backend.login.services;

import io.dcbn.backend.login.models.DcbnUser;
import io.dcbn.backend.login.repositories.DcbnUserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DcbnUserDetailsService implements UserDetailsService {

  private final DcbnUserRepository dcbnUserRepository;

  @Autowired
  public DcbnUserDetailsService(DcbnUserRepository dcbnUserRepository) {
    this.dcbnUserRepository = dcbnUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<DcbnUser> user = dcbnUserRepository.findByUsernameOrEmail(username, username);
    return user.map(DcbnUser::toUserDetails).orElseThrow(() ->
        new UsernameNotFoundException("Couldn't find user with username or email: " + username));
  }

}
