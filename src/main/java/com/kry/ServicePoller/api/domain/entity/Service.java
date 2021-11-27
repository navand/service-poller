package com.kry.ServicePoller.api.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Service implements Serializable {

  private static final long serialVersionUID = 1169010391380979103L;

  @JsonProperty(value = "id")
  private long id;

  @JsonProperty(value = "user_id")
  private long userId;

  @JsonProperty(value = "name")
  private String name;

  @JsonProperty(value = "url")
  private String url;

  @JsonProperty(value = "status")
  private String status;

  @JsonProperty(value = "created_at")
  private LocalDateTime createdAt;

  @JsonProperty(value = "updated_at")
  private LocalDateTime updatedAt;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Service service = (Service) o;
    return id == service.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Service{" +
      "id=" + id +
      ", userId='" + userId + '\'' +
      ", name='" + name + '\'' +
      ", url='" + url + '\'' +
      ", status='" + status +
      '}';
  }
}
