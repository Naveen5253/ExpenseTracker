package com.example.expensetracker.ExpenseTracker.login.repository;

import com.example.expensetracker.ExpenseTracker.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
