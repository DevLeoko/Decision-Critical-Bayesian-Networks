package io.dcbn.backend.authentication.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;

@Entity
@Data
@NoArgsConstructor
public class DcbnUser {

    @Id
    @GeneratedValue
    private long id;

    @Unique
    @NotEmpty
    private String username;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    @NotNull
    private Role role;

    public DcbnUser(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserDetails toUserDetails() {
        return new User(username, password,
                Collections.singletonList(new SimpleGrantedAuthority(role.getName())));
    }

    public DcbnUser withEncryptedPassword(PasswordEncoder encoder) {
        return new DcbnUser(username, email, encoder.encode(password), role);
    }

}

