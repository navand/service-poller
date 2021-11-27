package com.kry.ServicePoller.verticle;

import com.kry.ServicePoller.api.domain.entity.Service;
import com.kry.ServicePoller.api.repository.ServiceRepository;
import com.kry.ServicePoller.utils.DbUtils;
import com.kry.ServicePoller.utils.LogUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mysqlclient.MySQLPool;

public class ServiceHealthCheckVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHealthCheckVerticle.class);

  @Override
  public void start(Promise<Void> promise) {
    final WebClient client = WebClient.create(vertx);
    final MySQLPool dbClient = DbUtils.buildDbClient(vertx);
    final ServiceRepository serviceRepository = new ServiceRepository();


    vertx.setPeriodic(10000, schedulerId -> {
      dbClient.getConnection().compose(connection -> serviceRepository.selectAll(connection).eventually(v -> connection.close()))
        .onSuccess(event -> event.forEach(service -> {
          client.getAbs(service.getUrl()).send(response -> {
            if (response.succeeded()) {
              service.setStatus(200 == response.result().statusCode() ? "OK" : "FAIL");
            } else {
              service.setStatus("FAIL");
            }
            dbClient.getConnection().compose(connection -> serviceRepository.update(connection, service).eventually(v -> connection.close()));
          });
        })).onFailure(throwable -> LOGGER.error(throwable.getMessage()));
    });

    LOGGER.info("Service Health Check Scheduler started");
    promise.complete();
  }
}
