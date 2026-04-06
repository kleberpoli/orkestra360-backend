package com.project.orkestra360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Orkestra360 application.
 */
@SpringBootApplication
public class Orkestra360Application {

  /**
   * The entry point of the Orkestra360 application. This class initializes the
   * Spring Boot framework, performs component scanning, and starts the embedded
   * web server.
   *
   * @param args Command line arguments passed during application startup.
   */
  public static void main(String[] args) {
    SpringApplication.run(Orkestra360Application.class, args);
  }
}
