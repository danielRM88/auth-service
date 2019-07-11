package com.rosato.auth.service.controllers;

import java.util.List;

import javax.validation.Valid;

import com.rosato.auth.service.models.User;
import com.rosato.auth.service.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
