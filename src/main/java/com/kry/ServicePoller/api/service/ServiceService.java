package com.kry.ServicePoller.api.service;

import com.kry.ServicePoller.api.domain.dto.ServiceGetAllResponse;
import com.kry.ServicePoller.api.domain.dto.ServiceGetByIdResponse;
import com.kry.ServicePoller.api.domain.entity.Service;
import com.kry.ServicePoller.api.repository.ServiceRepository;
import com.kry.ServicePoller.utils.LogUtils;
import com.kry.ServicePoller.utils.QueryUtils;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLPool;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceService.class);

  private final MySQLPool dbClient;
  private final ServiceRepository serviceRepository;

  public ServiceService(MySQLPool dbClient,
                     ServiceRepository serviceRepository) {
    this.dbClient = dbClient;
    this.serviceRepository = serviceRepository;
  }

  /**
   * Read all services using pagination
   *
   * @param p Page
   * @param l Limit
   * @return ServiceGetAllResponse
   */
  public Future<ServiceGetAllResponse> readAll(long userId) {
    return dbClient.withTransaction(
        connection -> {
          return serviceRepository.count(connection, userId)
            .flatMap(total ->
              serviceRepository.selectAllForUser(connection, userId)
                .map(result -> {
                  final List<ServiceGetByIdResponse> services = result.stream()
                    .map(ServiceGetByIdResponse::new)
                    .collect(Collectors.toList());

                  return new ServiceGetAllResponse(total, services);
                })
            );
        })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all services", success.getServices())))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all services", throwable.getMessage())));
  }

  /**
   * Read one service
   *
   * @param id Service ID
   * @return ServiceGetByIdResponse
   */
  public Future<ServiceGetByIdResponse> readOne(long userId, int id) {
    return dbClient.withTransaction(
        connection -> {
          return serviceRepository.selectById(connection, userId, id)
            .map(ServiceGetByIdResponse::new);
        })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read one service", success)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read one service", throwable.getMessage())));
  }

  /**
   * Create one service
   *
   * @param service Service
   * @return ServiceGetByIdResponse
   */
  public Future<ServiceGetByIdResponse> create(Service service) {
    return dbClient.withTransaction(
        connection -> serviceRepository.insert(connection, service).map(ServiceGetByIdResponse::new))
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Create one service", success)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Create one service", throwable.getMessage())));
  }

  /**
   * Update one service
   *
   * @param id   Service ID
   * @param service Service
   * @return ServiceGetByIdResponse
   */
  public Future<ServiceGetByIdResponse> update(int id,
                                            Service service) {
    service.setId(id);

    return dbClient.withTransaction(
        connection -> {
          return serviceRepository.update(connection, service)
            .map(ServiceGetByIdResponse::new);
        })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update one service", success)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update one service", throwable.getMessage())));
  }

  /**
   * Delete one service
   *
   * @param id Service ID
   * @return Void
   */
  public Future<Void> delete(long userId, Integer id) {
    return dbClient.withTransaction(
        connection -> {
          return serviceRepository.delete(connection, userId, id);
        })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete one service", id)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete one service", throwable.getMessage())));
  }

}
