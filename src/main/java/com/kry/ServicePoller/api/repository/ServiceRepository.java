package com.kry.ServicePoller.api.repository;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kry.ServicePoller.api.domain.entity.Service;
import com.kry.ServicePoller.utils.LogUtils;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.time.LocalDateTime;
import java.util.*;

public class ServiceRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRepository.class);

  private static final String SQL_SELECT_ALL = "SELECT * FROM services";
  private static final String SQL_SELECT_ALL_FOR_USER = "SELECT * FROM services WHERE user_id = #{user_id}";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM services WHERE id = #{id} AND user_id = #{user_id}";
  private static final String SQL_INSERT = "INSERT INTO services (user_id, name, url) " +
    "VALUES (#{user_id}, #{name}, #{url})";
  private static final String SQL_UPDATE = "UPDATE services SET user_id = #{user_id}, name = #{name}, url = #{url}, " +
    "status = #{status} WHERE id = #{id} AND user_id = #{user_id}";
  private static final String SQL_DELETE = "DELETE FROM services WHERE id = #{id} AND user_id = #{user_id}";
  private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM services WHERE user_id = #{user_id}";

  public ServiceRepository() {
    DatabindCodec.mapper().registerModule(new JavaTimeModule());
  }

  /**
   * Read all services using pagination
   *
   * @param connection PostgreSQL connection
   * @return List<Service>
   */
  public Future<List<Service>> selectAll(SqlConnection connection) {
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_ALL)
      .mapTo(Service.class)
      .execute(Map.of())
      .map(rowSet -> {
        final List<Service> services = new ArrayList<>();
        rowSet.forEach(services::add);

        return services;
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all services", SQL_SELECT_ALL)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all services", throwable.getMessage())));
  }

  /**
   * Read all services using pagination
   *
   * @param connection PostgreSQL connection
   * @return List<Service>
   */
  public Future<List<Service>> selectAllForUser(SqlConnection connection, long userId) {
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_ALL_FOR_USER)
      .mapTo(Service.class)
      .execute(Map.of("user_id", userId))
      .map(rowSet -> {
        final List<Service> services = new ArrayList<>();
        rowSet.forEach(services::add);

        return services;
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all services", SQL_SELECT_ALL)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all services", throwable.getMessage())));
  }

  /**
   * Read one service
   *
   * @param connection PostgreSQL connection
   * @param id         Service ID
   * @return Service
   */
  public Future<Service> selectById(SqlConnection connection,
                                 long userId,
                                 int id) {
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_BY_ID)
      .mapTo(Service.class)
      .execute(Map.of("user_id", userId, "id", id))
      .map(rowSet -> {
        final RowIterator<Service> iterator = rowSet.iterator();

        if (iterator.hasNext()) {
          return iterator.next();
        } else {
          throw new NoSuchElementException(LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(id));
        }
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read service by id", SQL_SELECT_BY_ID)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read service by id", throwable.getMessage())));
  }

  /**
   * Create one service
   *
   * @param connection PostgreSQL connection
   * @param service       Service
   * @return Service
   */
  public Future<Service> insert(SqlConnection connection,
                             Service service) {
    return SqlTemplate
      .forUpdate(connection, SQL_INSERT)
      .mapFrom(Service.class)
      .mapTo(Service.class)
      .execute(service)
      .map(rowSet -> {
        if (rowSet.rowCount() == 1) {
          service.setId(rowSet.property(MySQLClient.LAST_INSERTED_ID));
          service.setCreatedAt(LocalDateTime.now());
          return service;
        } else {
          throw new IllegalStateException(LogUtils.CANNOT_CREATE_SERVICE_MESSAGE.buildMessage());
        }
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Insert service", SQL_INSERT)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Insert service", throwable.getMessage())));
  }

  /**
   * Update one service
   *
   * @param connection PostgreSQL connection
   * @param service       Service
   * @return Service
   */
  public Future<Service> update(SqlConnection connection,
                             Service service) {
    return SqlTemplate
      .forUpdate(connection, SQL_UPDATE)
      .mapFrom(Service.class)
      .execute(service)
      .flatMap(rowSet -> {
        if (rowSet.rowCount() > 0) {
          return Future.succeededFuture(service);
        } else {
          throw new NoSuchElementException(LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(service.getId()));
        }
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update service", SQL_UPDATE)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update service", throwable.getMessage())));
  }

  /**
   * Update one service
   *
   * @param connection PostgreSQL connection
   * @param id         Service ID
   * @return Void
   */
  public Future<Void> delete(SqlConnection connection,
                             long userId,
                             int id) {
    return SqlTemplate
      .forUpdate(connection, SQL_DELETE)
      .execute(Map.of("user_id", userId, "id", id))
      .flatMap(rowSet -> {
        if (rowSet.rowCount() > 0) {
          LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete service", SQL_DELETE));
          return Future.succeededFuture();
        } else {
          LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete service", LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(id)));
          throw new NoSuchElementException(LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(id));
        }
      });
  }

  /**
   * Count all services
   *
   * @param connection PostgreSQL connection
   * @return Integer
   */
  public Future<Integer> count(SqlConnection connection, long userId) {
    final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");

    return SqlTemplate
      .forQuery(connection, SQL_COUNT)
      .mapTo(ROW_MAPPER)
      .execute(Collections.singletonMap("user_id", userId))
      .map(rowSet -> rowSet.iterator().next())
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Count services", SQL_COUNT)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Count service", throwable.getMessage())));
  }

}
