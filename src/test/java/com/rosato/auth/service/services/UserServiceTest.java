package com.rosato.auth.service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rosato.auth.service.models.User;
import com.rosato.auth.service.repositories.UserRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class UserServiceTest {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;
  private UserService userService;

  @BeforeAll
  public void init() {
    this.userService = new UserServiceImpl(userRepository, passwordEncoder);
    User user = new User();
    user.setFirstName("Daniel");
    user.setLastName("Rosato");
    user.setEmail("test@gmail.com");
    user.setPassword("password");
    this.userService.create(user);
  }

  @Test
  void injectedComponentsAreNotNull() {
    assertNotNull(userRepository);
    assertNotNull(passwordEncoder);
  }

  @Test
  void createsUser() {
    String email = "new-email@gmail.com";

    User user = new User();
    user.setFirstName("Celia");
    user.setLastName("Cruz");
    user.setEmail(email);
    user.setPassword("password");
    userService.create(user);

    User createdUser = this.userService.findByEmail(email);
    assertEquals(email, createdUser.getEmail());
  }

  @Test
  void updatesUser() {
    String email = "test@gmail.com";

    User user = this.userService.findByEmail(email);
    assertEquals("Daniel", user.getFirstName());
    user.setFirstName("NewDaniel");
    this.userService.update(user);
    assertEquals("NewDaniel", user.getFirstName());
  }

  @Test
  void throwsConstraintViolationForExistingUser() {
    User user = new User();
    user.setFirstName("Daniel");
    user.setLastName("Rosato");
    user.setEmail("test@gmail.com");
    user.setPassword("password");
    assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
      this.userService.create(user);
    });
  }

  @Test
  void encryptsUserPassword() {
    String email = "tpuente@gmail.com";

    User user = new User();
    user.setFirstName("Tito");
    user.setLastName("Puente");
    user.setEmail(email);
    user.setPassword("password");
    userService.create(user);

    User createdUser = this.userService.findByEmail(email);
    assertNotEquals("password", createdUser.getPassword());
    assertTrue(passwordEncoder.matches("password", createdUser.getPassword()));
  }
}