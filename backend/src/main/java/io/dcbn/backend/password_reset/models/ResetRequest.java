package io.dcbn.backend.password_reset.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetRequest {

    @NotBlank
    private String token;

    @NotBlank
    private String password;

}
