package com.example.finance_tracker.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance_tracker.entity.User;
public interface UserRepository extends JpaRepository<User, Long> {}