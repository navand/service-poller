package com.kry.ServicePoller.api.repository;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kry.ServicePoller.utils.LogUtils;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import com.kry.ServicePoller.api.domain.entity.User;

import java.util.*;

public class UserRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

  private static final String SQL_SELECT_ALL = "SELECT * FROM users";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM users WHERE id = #{id}";
  private static final String SQL_SELECT_BY_USERNAME = "SELECT * FROM users WHERE username = #{username}";
  private static final String SQL_INSERT = "INSERT INTO users (username, password) " +
    "VALUES (#{username}, #{password}) RETURNING id";
  private static final String SQL_UPDATE = "UPDATE users SET password = #{password} WHERE id = #{id}";
  private static final String SQL_DELETE = "DELETE FROM users WHERE id = #{id}";
  private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM users";

  public UserRepository() {
    DatabindCodec.mapper().registerModule(new JavaTimeModule());
  }

  /**
   * Read all users using pagination
   *
   * @param connection MySQL connection
   * @param limit      Limit
   * @param offset     Offset
   * @return List<User>
   */
  public Future<List<User>> selectAll(SqlConnection connection,
                                      int limit,
                                      int offset) {
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_ALL)
      .mapTo(User.class)
      .execute(Map.of("limit", limit, "offset", offset))
      .map(rowSet -> {
        final List<User> users = new ArrayList<>();
        rowSet.forEach(users::add);

        return users;
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all users", SQL_SELECT_ALL)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all users", throwable.getMessage())));
  }

  /**
   * Read one user
   *
   * @param connection MySQL connection
   * @param id         User ID
   * @return User
   */
  public Future<User> selectById(SqlConnection connection,
                                 int id) {
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_BY_ID)
      .mapTo(User.class)
      .execute(Collections.singletonMap("id", id))
      .map(rowSet -> {
        final RowIterator<User> iterator = rowSet.iterator();

        if (iterator.hasNext()) {
          return iterator.next();
        } else {
          throw new NoSuchElementException(LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(id));
        }
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read user by id", SQL_SELECT_BY_ID)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read user by id", throwable.getMessage())));
  }

  /**
   * Read one user
   *
   * @param connection MySQL connection
   * @param username         User username
   * @return User
   */
  public Future<User> selectByUsername(SqlConnection connection,
                                 String username) {
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_BY_USERNAME)
      .mapTo(User.class)
      .execute(Collections.singletonMap("username", username))
      .map(rowSet -> {
        final RowIterator<User> iterator = rowSet.iterator();

        if (iterator.hasNext()) {
          return iterator.next();
        } else {
          throw new NoSuchElementException(LogUtils.NO_USER_WITH_USERNAME_MESSAGE.buildMessage(username));
        }
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read user by username", SQL_SELECT_BY_ID)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read user by username", throwable.getMessage())));
  }

  /**
   * Create one user
   *
   * @param connection MySQL connection
   * @param user       User
   * @return User
   */
  public Future<User> insert(SqlConnection connection,
                             User user) {
    return SqlTemplate
      .forUpdate(connection, SQL_INSERT)
      .mapFrom(User.class)
      .mapTo(User.class)
      .execute(user)
      .map(rowSet -> {
        final RowIterator<User> iterator = rowSet.iterator();

        if (iterator.hasNext()) {
          user.setId(iterator.next().getId());
          return user;
        } else {
          throw new IllegalStateException(LogUtils.CANNOT_CREATE_SERVICE_MESSAGE.buildMessage());
        }
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Insert user", SQL_INSERT)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Insert user", throwable.getMessage())));
  }

  /**
   * Update one user
   *
   * @param connection MySQL connection
   * @param user       User
   * @return User
   */
  public Future<User> update(SqlConnection connection,
                             User user) {
    return SqlTemplate
      .forUpdate(connection, SQL_UPDATE)
      .mapFrom(User.class)
      .execute(user)
      .flatMap(rowSet -> {
        if (rowSet.rowCount() > 0) {
          return Future.succeededFuture(user);
        } else {
          throw new NoSuchElementException(LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(user.getId()));
        }
      })
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update user", SQL_UPDATE)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update user", throwable.getMessage())));
  }

  /**
   * Update one user
   *
   * @param connection MySQL connection
   * @param id         User ID
   * @return Void
   */
  public Future<Void> delete(SqlConnection connection,
                             int id) {
    return SqlTemplate
      .forUpdate(connection, SQL_DELETE)
      .execute(Collections.singletonMap("id", id))
      .flatMap(rowSet -> {
        if (rowSet.rowCount() > 0) {
          LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete user", SQL_DELETE));
          return Future.succeededFuture();
        } else {
          LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete user", LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(id)));
          throw new NoSuchElementException(LogUtils.NO_SERVICE_WITH_ID_MESSAGE.buildMessage(id));
        }
      });
  }

  /**
   * Count all users
   *
   * @param connection MySQL connection
   * @return Integer
   */
  public Future<Integer> count(SqlConnection connection) {
    final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");

    return SqlTemplate
      .forQuery(connection, SQL_COUNT)
      .mapTo(ROW_MAPPER)
      .execute(Collections.emptyMap())
      .map(rowSet -> rowSet.iterator().next())
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Count users", SQL_COUNT)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Count user", throwable.getMessage())));
  }

}
