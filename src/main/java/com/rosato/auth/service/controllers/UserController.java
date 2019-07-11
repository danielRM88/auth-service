package com.rosato.auth.service.controllers;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.util.Date;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rosato.auth.service.config.JWTCreationFilter;
import com.rosato.auth.service.models.User;
import com.rosato.auth.service.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController {
  private UserService users;

  public UserController(UserService users) {
    this.users = users;
  }

  @GetMapping("")
  public ResponseEntity<List<User>> index() {
    return new ResponseEntity<>(this.users.findAll(), HttpStatus.OK);
  }

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@Valid @RequestBody User user) {
    User newUser = this.users.create(user);
    return newUser;
  }

  @PostMapping("/reset-password")
  public String reset(@RequestBody ObjectNode body) throws UserNotFoundException {
    String email = body.get("email").asText();
    // Url of the front end to redirect the user so that he can type new password
    String redirectUrl = body.get("redirect_url").asText();
    User user = this.users.findByEmail(email);

    if (user == null) {
      throw new UserNotFoundException();
    }

    String token = JWT.create().withSubject(user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + JWTCreationFilter.EXPIRATION_TIME))
        .sign(HMAC512(JWTCreationFilter.SECRET.getBytes()));

    // send email
    return redirectUrl + "?token=" + token;
  }

  @PostMapping("/confirm-reset-password")
  public void confirmReset(@RequestBody ObjectNode body, HttpServletRequest request)
      throws UserNotFoundException, InvalidAttributeValueException {
    String token = request.getHeader(JWTCreationFilter.HEADER_STRING);

    if (token != null) {
      String password = body.get("password").asText();
      String passwordConfirmation = body.get("password_confirmation").asText();

      if (!password.equals(passwordConfirmation)) {
        throw new InvalidAttributeValueException();
      }

      String email = JWT.require(Algorithm.HMAC512(JWTCreationFilter.SECRET.getBytes())).build()
          .verify(token.replace(JWTCreationFilter.TOKEN_PREFIX, "")).getSubject();

      User user = this.users.findByEmail(email);

      if (user == null) {
        throw new UserNotFoundException();
      }

      user.setPassword(password);
      this.users.update(user);
    } else {
      throw new UsernameNotFoundException("token missing");
    }
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "video not found")
  public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
  }
}
