package com.kry.ServicePoller.api.handler;

import com.kry.ServicePoller.api.domain.dto.UserResponse;
import com.kry.ServicePoller.api.domain.entity.Service;
import com.kry.ServicePoller.api.domain.entity.User;
import com.kry.ServicePoller.api.service.UserService;
import com.kry.ServicePoller.utils.ResponseUtils;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class UserHandler {

  private static final String ID_PARAMETER = "id";
  private static final String PAGE_PARAMETER = "page";
  private static final String LIMIT_PARAMETER = "limit";

  private final UserService userService;

  public UserHandler(UserService userService) {
    this.userService = userService;
  }

  /**
   * Read all books
   * It should return 200 OK in case of success
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param rc Routing context
   * @return BookGetAllResponse
   */
  public Future<UserResponse> login(RoutingContext rc) {
    final User user = rc.getBodyAsJson().mapTo(User.class);

    return userService.login(user)
      .onSuccess(success -> ResponseUtils.buildOkResponse(rc, success))
      .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
  }
}
