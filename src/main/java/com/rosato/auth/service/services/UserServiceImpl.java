package com.rosato.auth.service.services;

import java.util.List;

import com.rosato.auth.service.models.User;
import com.rosato.auth.service.repositories.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private BCryptPasswordEncoder passwordEncoder;
  private UserRepository repo;

  public UserServiceImpl(UserRepository repo, BCryptPasswordEncoder passwordEncoder) {
    this.repo = repo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User create(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User newUser = this.repo.save(user);
    return newUser;
  }

  @Override
  public List<User> findAll() {
    return repo.findAll();
  }

  @Override
  public User findByEmail(String email) {
    return repo.findByEmail(email);
  }
}