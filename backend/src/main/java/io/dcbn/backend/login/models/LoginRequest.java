package io.dcbn.backend.login.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

  private String username;
  private String password;
}
