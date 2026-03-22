package com.example.expensetracker.ExpenseTracker.login.service;

import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public void register(User user) {
        repo.save(user);
    }

    public User login(String email, String password) {
        User user = repo.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    public void updateName(User user, String newName) {
        user.setName(newName);
        repo.save(user);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        repo.save(user);
    }

    public void updateCurrency(User user, String currency) {
        user.setCurrency(currency);
        repo.save(user);
    }

    public void updateEmail(User user, String newEmail) {
        user.setEmail(newEmail);
        repo.save(user);
    }
}