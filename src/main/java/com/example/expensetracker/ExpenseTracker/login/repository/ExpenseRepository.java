package com.example.expensetracker.ExpenseTracker.login.repository;


import com.example.expensetracker.ExpenseTracker.login.model.Expense;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);

    @Transactional
    void deleteByUser(User user);
}
