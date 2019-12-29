package io.dcbn.backend.authentication.models;

public enum Role {
  MODERATOR("ROLE_MODERATOR"), ADMIN("ROLE_ADMIN"), SUPERADMIN("ROLE_SUPERADMIN");

  private final String name;

  Role(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
