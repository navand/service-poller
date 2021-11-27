package com.kry.ServicePoller.verticle;

import com.kry.ServicePoller.api.handler.*;
import com.kry.ServicePoller.api.repository.ServiceRepository;
import com.kry.ServicePoller.api.repository.UserRepository;
import com.kry.ServicePoller.api.router.HealthCheckRouter;
import com.kry.ServicePoller.api.router.MetricsRouter;
import com.kry.ServicePoller.api.router.ServiceRouter;
import com.kry.ServicePoller.api.router.UserRouter;
import com.kry.ServicePoller.api.service.ServiceService;
import com.kry.ServicePoller.api.service.UserService;
import com.kry.ServicePoller.utils.DbUtils;
import com.kry.ServicePoller.utils.LogUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.mysqlclient.MySQLPool;

public class ApiVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);

  @Override
  public void start(Promise<Void> promise) {
    final MySQLPool dbClient = DbUtils.buildDbClient(vertx);

    // Authentication provider
    JWTAuth authProvider = JWTAuth.create(vertx, new JWTAuthOptions()
      .addPubSecKey(new PubSecKeyOptions()
        .setAlgorithm("HS256")
        .setBuffer("change me")));

    // User API
    final UserRepository userRepository = new UserRepository();
    final UserService userService = new UserService(dbClient, userRepository, authProvider);
    final UserHandler userHandler = new UserHandler(userService);
    final UserValidationHandler userValidationHandler = new UserValidationHandler(vertx);
    final UserRouter userRouter = new UserRouter(vertx, userHandler, userValidationHandler);

    // Service API
    final ServiceRepository serviceRepository = new ServiceRepository();
    final ServiceService serviceService = new ServiceService(dbClient, serviceRepository);
    final ServiceHandler serviceHandler = new ServiceHandler(serviceService);
    final ServiceValidationHandler serviceValidationHandler = new ServiceValidationHandler(vertx);
    final ServiceRouter serviceRouter = new ServiceRouter(vertx, serviceHandler, serviceValidationHandler, authProvider);

    final Router router = Router.router(vertx);
    ErrorHandler.buildHandler(router);
    HealthCheckRouter.setRouter(vertx, router, dbClient);
    MetricsRouter.setRouter(router);
    userRouter.setRouter(router);
    serviceRouter.setRouter(router);

    buildHttpServer(vertx, promise, router);
  }

  /**
   * Run HTTP server on port 8080 with specified routes
   *
   * @param vertx   Vertx context
   * @param promise Callback
   * @param router  Router
   */
  private void buildHttpServer(Vertx vertx,
                               Promise<Void> promise,
                               Router router) {
    final int port = 8080;

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port, http -> {
        if (http.succeeded()) {
          promise.complete();
          LOGGER.info(LogUtils.RUN_HTTP_SERVER_SUCCESS_MESSAGE.buildMessage(port));
        } else {
          promise.fail(http.cause());
          LOGGER.info(LogUtils.RUN_HTTP_SERVER_ERROR_MESSAGE.buildMessage());
        }
      });
  }
}
