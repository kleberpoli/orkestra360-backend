package com.project.orkestra360;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Application Context Load Test")
class Orkestra360ApplicationTests {

  @Test
  @DisplayName("Should execute main method for coverage purposes")
  void mainMethodTest() {

    // Act: invoke application entry point
    Orkestra360Application.main(new String[] {});

    // Assert: no exception means successful execution
  }
}
