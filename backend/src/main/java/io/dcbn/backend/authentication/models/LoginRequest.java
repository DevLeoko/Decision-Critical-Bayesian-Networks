package io.dcbn.backend.authentication.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

  private String username;
  private String password;
}
