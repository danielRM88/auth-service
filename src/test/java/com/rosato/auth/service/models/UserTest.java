package com.rosato.auth.service.models;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {

  @DisplayName("User Model Tests")
  @Test
  void testEncryptedPassword() {
    User user = new User();
    user.setFirstName("Daniel");
    user.setLastName("Rosato");
    user.setEmail("test@gmail.com");
    user.setPassword("password");
    assertNotEquals("password", user.getPassword());
  }

}