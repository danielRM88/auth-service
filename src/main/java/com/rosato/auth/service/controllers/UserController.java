package com.rosato.auth.service.controllers;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
  private JavaMailSender javaMailSender;
  private UserService users;

  public UserController(UserService users, JavaMailSender javaMailSender) {
    this.users = users;
    this.javaMailSender = javaMailSender;
  }

  @GetMapping("")
  public List<User> index() {
    return this.users.findAll();
  }

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@Valid @RequestBody User user) {
    User newUser = this.users.create(user);
    return newUser;
  }

  @PostMapping("/reset-password")
  public void reset(@RequestBody ObjectNode body) throws UserNotFoundException, MessagingException {
    String email = body.get("email").asText();
    // Url of the front end to redirect the user so that he can type new password
    String redirectUrl = body.get("redirect_url").asText();
    User user = this.users.findByEmail(email);

    if (user == null) {
      throw new UserNotFoundException();
    }

    String token = JWT.create().withSubject(user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + JWTCreationFilter.EXPIRATION_TIME))
        .sign(HMAC512(JWTCreationFilter.RESET_PASSWORD_SECRET.getBytes()));

    MimeMessage msg = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(msg, true);

    helper.setTo(email);

    helper.setSubject("Password Reset");
    String link = redirectUrl + "?token=" + token;
    String emailBody = "<h2>Reset your password by clicking in the following link</h2><a href=\"" + link
        + "\">Reset password</a>";
    helper.setText(emailBody, true);

    javaMailSender.send(msg);
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

      String email = JWT.require(Algorithm.HMAC512(JWTCreationFilter.RESET_PASSWORD_SECRET.getBytes())).build()
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
