package com.kry.ServicePoller;

import com.kry.ServicePoller.verticle.ApiVerticle;
import com.kry.ServicePoller.verticle.MigrationVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(VertxExtension.class)
public class TestApiVerticle extends AbstractContainerBaseTest {

  @BeforeAll
  static void setup(Vertx vertx,
                    VertxTestContext testContext) {
    vertx.deployVerticle(new MigrationVerticle(), testContext.succeeding(migrationVerticleId ->
      vertx.deployVerticle(new ApiVerticle(), testContext.succeeding(apiVerticleId ->
        testContext.completeNow()))));
  }

  @Test
  @Order(1)
  @DisplayName("Login services")
  void login(Vertx vertx, VertxTestContext testContext) throws IOException {
    final WebClient webClient = WebClient.create(vertx);
    final JsonObject body = readFileAsJsonObject("src/test/resources/login/request.json");

    webClient.post(8080, "localhost", "/api/v1/users/login")
      .as(BodyCodec.jsonObject())
      .sendJsonObject(body, testContext.succeeding(response -> {
          testContext.verify(() ->
              Assertions.assertAll(
                () -> Assertions.assertEquals(200, response.statusCode()),
                () -> Assertions.assertEquals(readFileAsJsonObject("src/test/resources/login/response.json"), response.body().putNull("token"))
              )
          );

          testContext.completeNow();
        })
      );
  }

  @Test
  @Order(5)
  @DisplayName("Read all services")
  void readAll(Vertx vertx, VertxTestContext testContext) {
    final WebClient webClient = WebClient.create(vertx);

    webClient.get(8080, "localhost", "/api/v1/services")
      .as(BodyCodec.jsonObject())
      .putHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYzODE5OTkxOX0.70mJ4Qg9nBJTGcHft6_Vghrt_jfiw7hd5jJDCqOFLIo")
      .send(testContext.succeeding(response -> {
          testContext.verify(() ->
            Assertions.assertAll(
              () -> Assertions.assertEquals(200, response.statusCode()),
              () -> Assertions.assertEquals(readFileAsJsonObject("src/test/resources/readAll/response.json"), response.body().put("services", new JsonArray().add(response.body().getJsonArray("services").getJsonObject(0).put("created_at", null))))
            )
          );

          testContext.completeNow();
        })
      );
  }

  @Test
  @Order(4)
  @DisplayName("Read one service")
  void readOne(Vertx vertx,
               VertxTestContext testContext) {
    final WebClient webClient = WebClient.create(vertx);

    webClient.get(8080, "localhost", "/api/v1/services/1")
      .as(BodyCodec.jsonObject())
      .putHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYzODE5OTkxOX0.70mJ4Qg9nBJTGcHft6_Vghrt_jfiw7hd5jJDCqOFLIo")
      .send(testContext.succeeding(response -> {
          testContext.verify(() ->
            Assertions.assertAll(
              () -> Assertions.assertEquals(200, response.statusCode()),
              () -> Assertions.assertEquals(readFileAsJsonObject("src/test/resources/readOne/response.json"), response.body().putNull("created_at"))
            )
          );

          testContext.completeNow();
        })
      );
  }

  @Test
  @Order(2)
  @DisplayName("Create service")
  void create(Vertx vertx,
              VertxTestContext testContext) throws IOException {
    final WebClient webClient = WebClient.create(vertx);
    final JsonObject body = readFileAsJsonObject("src/test/resources/create/request.json");

    webClient.post(8080, "localhost", "/api/v1/services")
      .as(BodyCodec.jsonObject())
      .putHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYzODE5OTkxOX0.70mJ4Qg9nBJTGcHft6_Vghrt_jfiw7hd5jJDCqOFLIo")
      .sendJsonObject(body, testContext.succeeding(response -> {
          testContext.verify(() ->
            Assertions.assertAll(
              () -> Assertions.assertEquals(201, response.statusCode()),
              () -> Assertions.assertEquals(readFileAsJsonObject("src/test/resources/create/response.json"), response.body().putNull("created_at"))
            )
          );

          testContext.completeNow();
        })
      );
  }

  @Test
  @Order(3)
  @DisplayName("Update service")
  void update(Vertx vertx,
              VertxTestContext testContext) throws IOException {
    final WebClient webClient = WebClient.create(vertx);
    final JsonObject body = readFileAsJsonObject("src/test/resources/update/request.json");

    webClient.put(8080, "localhost", "/api/v1/services/1")
      .as(BodyCodec.jsonObject())
      .putHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYzODE5OTkxOX0.70mJ4Qg9nBJTGcHft6_Vghrt_jfiw7hd5jJDCqOFLIo")
      .sendJsonObject(body, testContext.succeeding(response -> {
          testContext.verify(() ->
            Assertions.assertAll(
              () -> Assertions.assertEquals(200, response.statusCode()),
              () -> Assertions.assertEquals(readFileAsJsonObject("src/test/resources/update/response.json"), response.body())
            )
          );

          testContext.completeNow();
        })
      );
  }

  @Test
  @Order(6)
  @DisplayName("Delete service")
  void delete(Vertx vertx,
              VertxTestContext testContext) {
    final WebClient webClient = WebClient.create(vertx);

    webClient.delete(8080, "localhost", "/api/v1/services/1")
      .putHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYzODE5OTkxOX0.70mJ4Qg9nBJTGcHft6_Vghrt_jfiw7hd5jJDCqOFLIo")
      .send(testContext.succeeding(response -> {
          testContext.verify(() ->
            Assertions.assertEquals(204, response.statusCode())
          );

          testContext.completeNow();
        })
      );
  }

  private JsonObject readFileAsJsonObject(String path) throws IOException {
    return new JsonObject(Files.lines(Paths.get(path), StandardCharsets.UTF_8).collect(Collectors.joining("\n")));
  }
}
