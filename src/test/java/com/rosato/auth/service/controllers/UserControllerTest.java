package com.rosato.auth.service.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.rosato.auth.service.models.User;
import com.rosato.auth.service.services.UserService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class UserControllerTest {

  // bind the above RANDOM_PORT
  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Autowired
  private UserService userService;

  @BeforeAll
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()) // enable security for the mock set up
        .build();
  }

  @Test
  public void testIndex() throws Exception {
    User user = new User();
    user.setEmail("test@gmail.com");
    user.setPassword("password");
    user.setFirstName("Daniel");
    user.setLastName("Rosato");
    userService.create(user);

    String token = mvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("{\"email\": \"test@gmail.com\", \"password\": \"password\"}"))
        .andExpect(status().isOk()).andReturn().getResponse().getHeader("Authorization");

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", token);
    HttpEntity<?> entity = new HttpEntity<>(headers);
    String url = new URL("http://localhost:" + port + "/v1/users/").toString();

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    String responseStr = response.getBody();
    JsonArray jsonResponse = new JsonParser().parse(responseStr).getAsJsonArray();

    assertEquals(1, jsonResponse.size());
    assertEquals("test@gmail.com", jsonResponse.get(0).getAsJsonObject().get("email").getAsString());
  }

  @Test
  public void testCreate() throws Exception {
    User user = new User();
    user.setEmail("test-create@gmail.com");
    user.setPassword("password");
    user.setFirstName("Daniel");
    user.setLastName("Rosato");

    HttpEntity<User> entity = new HttpEntity<>(user);
    String url = new URL("http://localhost:" + port + "/v1/users").toString();

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void testAuthentication() throws MalformedURLException {
    String url = new URL("http://localhost:" + port + "/v1/users/").toString();

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }
}