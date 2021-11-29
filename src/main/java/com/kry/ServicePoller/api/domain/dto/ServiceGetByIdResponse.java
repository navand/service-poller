package com.kry.ServicePoller.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import com.kry.ServicePoller.api.domain.entity.Service;

public class ServiceGetByIdResponse implements Serializable {

  private static final long serialVersionUID = 7621071075786169611L;

  @JsonProperty(value = "id")
  private final long id;

  @JsonProperty(value = "user_id")
  private final long userId;

  @JsonProperty(value = "name")
  private final String name;

  @JsonProperty(value = "url")
  private final String url;

  @JsonProperty(value = "status")
  private final String status;

  @JsonProperty(value = "created_at")
  private final String createdAt;

  public ServiceGetByIdResponse(Service service) {
    this.id = service.getId();
    this.userId = service.getUserId();
    this.name = service.getName();
    this.url = service.getUrl();
    this.status = service.getStatus();
    this.createdAt = service.getCreatedAt() == null ? null : service.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  public long getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getStatus() {
    return status;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServiceGetByIdResponse that = (ServiceGetByIdResponse) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ServiceResponse{" +
      "id=" + id +
      ", userId='" + userId + '\'' +
      ", name='" + name + '\'' +
      ", url='" + url + '\'' +
      ", status='" + status + '\'' +
      ", createdAt='" + createdAt +
      '}';
  }
}
