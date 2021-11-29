package com.kry.ServicePoller.api.service;

import com.kry.ServicePoller.api.domain.dto.UserResponse;
import com.kry.ServicePoller.api.domain.entity.User;
import com.kry.ServicePoller.api.repository.UserRepository;
import com.kry.ServicePoller.utils.LogUtils;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.HashingStrategy;
import io.vertx.ext.auth.authentication.CredentialValidationException;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.mysqlclient.MySQLPool;

import java.util.Base64;

public class UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  String salt = Base64.getEncoder().encodeToString("change.me".getBytes());

  private final MySQLPool dbClient;
  private final UserRepository userRepository;
  private final JWTAuth authProvider;

  public UserService(MySQLPool dbClient,
                     UserRepository userRepository, JWTAuth authProvider) {
    this.dbClient = dbClient;
    this.userRepository = userRepository;
    this.authProvider = authProvider;
  }


  /**
   * Read one user
   *
   * @param user User
   * @return UserResponse
   */
  public Future<UserResponse> login(User user) {
    return dbClient.withTransaction(
        connection -> userRepository.selectByUsername(connection, user.getUsername())
            .map(u -> {
              HashingStrategy strategy = HashingStrategy.load();
              if(!strategy.verify(u.getPassword(), user.getPassword())) {
//                return null;
                throw new CredentialValidationException(LogUtils.AUTHENTICATION_FAILED.buildMessage());
              }
                String token = authProvider.generateToken(new JsonObject().put("userId", u.getId()));
                return new UserResponse(u, token);
            })
        )
      .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read one user", success)))
      .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read one user", throwable.getMessage())));
  }
}
