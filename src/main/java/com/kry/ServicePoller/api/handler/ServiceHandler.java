package com.kry.ServicePoller.api.handler;

import com.kry.ServicePoller.api.domain.dto.ServiceGetAllResponse;
import com.kry.ServicePoller.api.domain.dto.ServiceGetByIdResponse;
import com.kry.ServicePoller.api.domain.entity.Service;
import com.kry.ServicePoller.api.service.ServiceService;
import com.kry.ServicePoller.utils.ResponseUtils;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class ServiceHandler {

  private static final String ID_PARAMETER = "id";

  private final ServiceService serviceService;

  public ServiceHandler(ServiceService serviceService) {
    this.serviceService = serviceService;
  }

  /**
   * Read all services
   * It should return 200 OK in case of success
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param rc Routing context
   * @return ServiceGetAllResponse
   */
  public Future<ServiceGetAllResponse> readAll(RoutingContext rc) {
    final long userId = Long.parseLong(rc.user().principal().getString("userId"));

    return serviceService.readAll(userId)
      .onSuccess(success -> ResponseUtils.buildOkResponse(rc, success))
      .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
  }

  /**
   * Read one service
   * It should return 200 OK in case of success
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param rc Routing context
   * @return ServiceGetByIdResponse
   */
  public Future<ServiceGetByIdResponse> readOne(RoutingContext rc) {
    final String id = rc.pathParam(ID_PARAMETER);
    final long userId = Long.parseLong(rc.user().principal().getString("userId"));

    return serviceService.readOne(userId, Integer.parseInt(id))
      .onSuccess(success -> ResponseUtils.buildOkResponse(rc, success))
      .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
  }

  /**
   * Create one service
   * It should return 201 Created in case of success
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param rc Routing context
   * @return ServiceGetByIdResponse
   */
  public Future<ServiceGetByIdResponse> create(RoutingContext rc) {
    final long userId = Long.parseLong(rc.user().principal().getString("userId"));
    Service service = rc.getBodyAsJson().mapTo(Service.class);
    service.setUserId(userId);

    return serviceService.create(service)
      .onSuccess(success -> ResponseUtils.buildCreatedResponse(rc, success))
      .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
  }

  /**
   * Update one service
   * It should return 200 OK in case of success
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param rc Routing context
   * @return ServiceGetByIdResponse
   */
  public Future<ServiceGetByIdResponse> update(RoutingContext rc) {
    final String id = rc.pathParam(ID_PARAMETER);
    final long userId = Long.parseLong(rc.user().principal().getString("userId"));
    Service service = rc.getBodyAsJson().mapTo(Service.class);
    service.setUserId(userId);

    return serviceService.update(Integer.parseInt(id), service)
      .onSuccess(success -> ResponseUtils.buildOkResponse(rc, success))
      .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
  }

  /**
   * Delete one service
   * It should return 204 No Content in case of success
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param rc Routing context
   * @return ServiceGetByIdResponse
   */
  public Future<Void> delete(RoutingContext rc) {
    final String id = rc.pathParam(ID_PARAMETER);
    final long userId = Long.parseLong(rc.user().principal().getString("userId"));

    return serviceService.delete(userId, Integer.parseInt(id))
      .onSuccess(success -> ResponseUtils.buildNoContentResponse(rc))
      .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
  }

}
