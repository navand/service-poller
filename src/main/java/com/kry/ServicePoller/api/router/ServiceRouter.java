package com.kry.ServicePoller.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import com.kry.ServicePoller.api.handler.ServiceHandler;
import com.kry.ServicePoller.api.handler.ServiceValidationHandler;

public class ServiceRouter {

  private final Vertx vertx;
  private final ServiceHandler serviceHandler;
  private final ServiceValidationHandler userValidationHandler;
  private final JWTAuth authProvider;

  public ServiceRouter(Vertx vertx,
                       ServiceHandler serviceHandler,
                       ServiceValidationHandler userValidationHandler,
                       JWTAuth authProvider) {
    this.vertx = vertx;
    this.serviceHandler = serviceHandler;
    this.userValidationHandler = userValidationHandler;
    this.authProvider = authProvider;
  }

  /**
   * Set services API routes
   *
   * @param router Router
   */
  public void setRouter(Router router) {
    router.mountSubRouter("/api/v1", buildServiceRouter());
  }

  /**
   * Build services API
   * All routes are composed by an error handler, a validation handler and the actual business logic handler
   */
  private Router buildServiceRouter() {
    final Router serviceRouter = Router.router(vertx);

    serviceRouter.route("/services*").handler(BodyHandler.create()).handler(JWTAuthHandler.create(authProvider));
    serviceRouter.get("/services").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(userValidationHandler.readAll()).handler(serviceHandler::readAll);
    serviceRouter.get("/services/:id").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(userValidationHandler.readOne()).handler(serviceHandler::readOne);
    serviceRouter.post("/services").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(userValidationHandler.create()).handler(serviceHandler::create);
    serviceRouter.put("/services/:id").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(userValidationHandler.update()).handler(serviceHandler::update);
    serviceRouter.delete("/services/:id").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(userValidationHandler.delete()).handler(serviceHandler::delete);

    return serviceRouter;
  }

}
