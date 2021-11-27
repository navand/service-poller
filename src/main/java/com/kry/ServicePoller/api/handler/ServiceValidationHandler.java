package com.kry.ServicePoller.api.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.web.validation.RequestPredicate;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.ParameterProcessorFactory;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;

import java.util.regex.Pattern;

import static io.vertx.json.schema.common.dsl.Keywords.*;
import static io.vertx.json.schema.common.dsl.Schemas.*;

public class ServiceValidationHandler {

  private final Vertx vertx;

  public ServiceValidationHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  /**
   * Build read all services request validation
   *
   * @return ValidationHandler
   */
  public ValidationHandler readAll() {
    final SchemaParser schemaParser = buildSchemaParser();

    return ValidationHandler
      .builder(schemaParser)
      .build();
  }

  /**
   * Build read one service request validation
   *
   * @return ValidationHandler
   */
  public ValidationHandler readOne() {
    final SchemaParser schemaParser = buildSchemaParser();

    return ValidationHandler
      .builder(schemaParser)
      .pathParameter(buildIdPathParameter())
      .build();
  }

  /**
   * Build create one service request validation
   *
   * @return ValidationHandler
   */
  public ValidationHandler create() {
    final SchemaParser schemaParser = buildSchemaParser();
    final ObjectSchemaBuilder schemaBuilder = buildBodySchemaBuilder();

    return ValidationHandler
      .builder(schemaParser)
      .predicate(RequestPredicate.BODY_REQUIRED)
      .body(Bodies.json(schemaBuilder))
      .build();
  }

  /**
   * Build update one service request validation
   *
   * @return ValidationHandler
   */
  public ValidationHandler update() {
    final SchemaParser schemaParser = buildSchemaParser();
    final ObjectSchemaBuilder schemaBuilder = buildBodySchemaBuilder();

    return ValidationHandler
      .builder(schemaParser)
      .predicate(RequestPredicate.BODY_REQUIRED)
      .body(Bodies.json(schemaBuilder))
      .pathParameter(buildIdPathParameter())
      .build();
  }

  /**
   * Build delete one service request validation
   *
   * @return ValidationHandler
   */
  public ValidationHandler delete() {
    final SchemaParser schemaParser = buildSchemaParser();

    return ValidationHandler
      .builder(schemaParser)
      .pathParameter(buildIdPathParameter())
      .build();
  }

  private SchemaParser buildSchemaParser() {
    return SchemaParser.createDraft7SchemaParser(SchemaRouter.create(vertx, new SchemaRouterOptions()));
  }

  private ObjectSchemaBuilder buildBodySchemaBuilder() {
    return objectSchema()
      .requiredProperty("name", stringSchema().with(minLength(1)).with(maxLength(255)))
      .requiredProperty("url", stringSchema().with(minLength(5)).with(maxLength(255)).with(pattern(Pattern.compile("^(http:\\/\\/|https:\\/\\/)(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$"))));
  }

  private ParameterProcessorFactory buildIdPathParameter() {
    return Parameters.param("id", intSchema());
  }
}
