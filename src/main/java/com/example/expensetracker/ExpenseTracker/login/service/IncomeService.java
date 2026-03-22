package com.example.expensetracker.ExpenseTracker.login.service;


import com.example.expensetracker.ExpenseTracker.login.model.Income;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository repo;

    public void saveIncome(Income income) {
        repo.save(income);
    }

    public List<Income> getIncome(User user) {
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