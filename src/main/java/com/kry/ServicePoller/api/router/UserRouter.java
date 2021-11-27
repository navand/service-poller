package com.kry.ServicePoller.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import com.kry.ServicePoller.api.handler.UserHandler;
import com.kry.ServicePoller.api.handler.UserValidationHandler;

public class UserRouter {

  private final Vertx vertx;
  private final UserHandler userHandler;
  private final UserValidationHandler userValidationHandler;

  public UserRouter(Vertx vertx,
                    UserHandler userHandler,
                    UserValidationHandler userValidationHandler) {
    this.vertx = vertx;
    this.userHandler = userHandler;
    this.userValidationHandler = userValidationHandler;
  }

  /**
   * Set users API routes
   *
   * @param router Router
   */
  public void setRouter(Router router) {
    router.mountSubRouter("/api/v1", buildUserRouter());
  }

  /**
   * Build users API
   * All routes are composed by an error handler, a validation handler and the actual business logic handler
   */
  private Router buildUserRouter() {
    final Router userRouter = Router.router(vertx);

    userRouter.route("/users*").handler(BodyHandler.create());
    userRouter.post("/users/login").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(userValidationHandler.login()).handler(userHandler::login);

    return userRouter;
  }

}
