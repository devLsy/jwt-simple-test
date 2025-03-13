package com.test.lsy.jwt1.repository;

import com.test.lsy.jwt1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);
}
