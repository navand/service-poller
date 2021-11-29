package com.kry.ServicePoller.verticle;

import com.kry.ServicePoller.utils.LogUtils;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> promise) {
    final long start = System.currentTimeMillis();

    deployMigrationVerticle(vertx)
      .flatMap(migrationVerticleId -> deployApiVerticle(vertx))
      .flatMap(migrationVerticleId -> deployServiceHealthCheckVerticle(vertx))
      .onSuccess(success -> LOGGER.info(LogUtils.RUN_APP_SUCCESSFULLY_MESSAGE.buildMessage(System.currentTimeMillis() - start)))
      .onFailure(throwable -> {
        LOGGER.error(throwable.getMessage());
        promise.fail(throwable);
      });
  }

  private Future<Void> deployMigrationVerticle(Vertx vertx) {
    final DeploymentOptions options = new DeploymentOptions()
      .setWorker(true)
      .setWorkerPoolName("migrations-worker-pool")
      .setInstances(1)
      .setWorkerPoolSize(1);

    return vertx.deployVerticle(MigrationVerticle.class.getName(), options)
      .flatMap(vertx::undeploy);
  }

  private Future<String> deployApiVerticle(Vertx vertx) {
    return vertx.deployVerticle(ApiVerticle.class.getName());
  }

  private Future<String> deployServiceHealthCheckVerticle(Vertx vertx) {
    final DeploymentOptions options = new DeploymentOptions()
      .setWorker(true)
      .setWorkerPoolName("service-health-check-worker-pool")
      .setInstances(1)
      .setWorkerPoolSize(1);

    return vertx.deployVerticle(ServiceHealthCheckVerticle.class.getName(), options);
  }
}
