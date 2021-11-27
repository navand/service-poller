package com.kry.ServicePoller.utils;

public enum LogUtils {

  REGULAR_CALL_SUCCESS_MESSAGE("%s called w/ success - %s"),
  REGULAR_CALL_ERROR_MESSAGE("%s called w/ error - %s"),
  NO_SERVICE_WITH_ID_MESSAGE("No service with id %d"),
  NO_USER_WITH_USERNAME_MESSAGE("No user with username %s"),
  CANNOT_CREATE_SERVICE_MESSAGE("Cannot create a new service"),
  RUN_HTTP_SERVER_SUCCESS_MESSAGE("HTTP server running on port %s"),
  RUN_HTTP_SERVER_ERROR_MESSAGE("Cannot run HTTP server"),
  NULL_OFFSET_ERROR_MESSAGE("Offset can't be null. Page %s and limit %s"),
  RUN_APP_SUCCESSFULLY_MESSAGE("vertx-4-reactive-rest-api started successfully in %d ms"),
  AUTHENTICATION_FAILED("Authentication Failed");

  private final String message;

  LogUtils(final String message) {
    this.message = message;
  }

  public String buildMessage(Object... argument) {
    return String.format(message, argument);
  }
}
