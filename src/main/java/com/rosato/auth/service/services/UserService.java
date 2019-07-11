package com.rosato.auth.service.services;

import java.util.List;

import com.rosato.auth.service.models.User;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
  List<User> findAll();

  User create(User user);

  User findByEmail(String email);
}