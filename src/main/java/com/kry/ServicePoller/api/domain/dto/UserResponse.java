package com.kry.ServicePoller.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kry.ServicePoller.api.domain.entity.User;

import java.io.Serializable;
import java.util.Objects;

public class UserResponse implements Serializable {

  private static final long serialVersionUID = -8964658883487451260L;

  @JsonProperty(value = "id")
  private final long id;

  @JsonProperty(value = "username")
  private final String username;

  @JsonProperty(value = "token")
  private String token;

  public UserResponse(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
  }

  public UserResponse(User user, String token) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.token = token;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getToken() {
    return token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserResponse that = (UserResponse) o;
    return username == that.username;
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return "UserResponse{" +
      "id=" + id +
      ", username=" + username +
      '}';
  }
}
