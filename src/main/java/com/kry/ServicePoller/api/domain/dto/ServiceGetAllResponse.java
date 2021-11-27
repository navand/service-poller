package com.kry.ServicePoller.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ServiceGetAllResponse implements Serializable {

  private static final long serialVersionUID = -8964658883487451260L;

  @JsonProperty(value = "total")
  private final int total;

  @JsonProperty(value = "services")
  private final List<ServiceGetByIdResponse> services;

  public ServiceGetAllResponse(int total,
                            List<ServiceGetByIdResponse> services) {
    this.total = total;
    this.services = services;
  }

  public int getTotal() {
    return total;
  }

  public List<ServiceGetByIdResponse> getServices() {
    return services;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServiceGetAllResponse that = (ServiceGetAllResponse) o;
    return total == that.total &&
      services.equals(that.services);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, services);
  }

  @Override
  public String toString() {
    return "ServiceGetAllResponse{" +
      "total=" + total +
      ", services=" + services +
      '}';
  }

}
