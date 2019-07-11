package com.rosato.auth.service.services;

import static java.util.Collections.emptyList;

import java.util.List;

import com.rosato.auth.service.models.User;
import com.rosato.auth.service.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  public User update(User user) {
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

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = repo.findByEmail(username);

    if (user == null) {
      throw new UsernameNotFoundException(username);
    }

    org.springframework.security.core.userdetails.User userDetail = new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), emptyList());
    return userDetail;
  }
}