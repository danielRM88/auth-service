package com.rosato.auth.service.services;

import java.util.List;

import com.rosato.auth.service.models.User;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
  List<User> findAll();

  User create(User user);

  User findByEmail(String email);
}