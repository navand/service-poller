package com.kry.ServicePoller;

import com.kry.ServicePoller.utils.ConfigUtils;
import org.testcontainers.containers.GenericContainer;

import java.util.Properties;

abstract class AbstractContainerBaseTest {

  static final GenericContainer MYSQL_CONTAINER;

  static {
    final Properties properties = ConfigUtils.getInstance().getProperties();

    MYSQL_CONTAINER = new GenericContainer<>("mysql:5.7")
      .withEnv("MYSQL_DATABASE", properties.getProperty("datasource.database"))
      .withEnv("MYSQL_USER", properties.getProperty("datasource.username"))
      .withEnv("MYSQL_PASSWORD", properties.getProperty("datasource.password"))
      .withEnv("MYSQL_ROOT_PASSWORD", properties.getProperty("datasource.password"))
      .withExposedPorts(Integer.parseInt(properties.getProperty("datasource.port")));

    MYSQL_CONTAINER.start();

    ConfigUtils.getInstance().getProperties().setProperty("datasource.port", String.valueOf(MYSQL_CONTAINER.getMappedPort(3306)));
  }

}
