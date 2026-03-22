package com.example.expensetracker.ExpenseTracker.login.service;

import com.example.expensetracker.ExpenseTracker.login.model.Expense;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository repo;

    public void saveExpense(Expense expense) {
        repo.save(expense);
    }

    public List<Expense> getExpenses(User user) {
        return repo.findByUser(user);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void deleteAllByUser(User user) {
        repo.deleteByUser(user);
    }
}
