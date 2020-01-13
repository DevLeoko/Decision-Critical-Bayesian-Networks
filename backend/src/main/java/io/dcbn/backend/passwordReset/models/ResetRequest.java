package io.dcbn.backend.passwordReset.models;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetRequest {

  @NotBlank
  private String token;

  @NotBlank
  private String password;

}
