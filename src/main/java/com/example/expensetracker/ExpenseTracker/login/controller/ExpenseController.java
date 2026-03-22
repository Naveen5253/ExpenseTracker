package com.example.expensetracker.ExpenseTracker.login.controller;


import com.example.expensetracker.ExpenseTracker.login.model.Expense;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    // Show Add Expense page
    @GetMapping("/add-expense")
    public String showForm(Model model) {
        model.addAttribute("expense", new Expense());

        return "add-expense";
    }

    // Save Expense
    @PostMapping("/expenses/add")
    public String addExpense(@RequestParam String category,
                             @RequestParam Double amount,
                             @RequestParam String date,
                             @RequestParam(required = false) String description,
                             @SessionAttribute("user") User user) {

        Expense expense = new Expense();
        expense.setCategory(category);
        expense.setAmount(amount);
        expense.setDate(LocalDate.parse(date));
        expense.setDescription(description);
        expense.setUser(user);  // link expense to logged-in user

        service.saveExpense(expense);  // actually save to DB

        return "redirect:/expenses";
    }

    // View Expenses
    @GetMapping("/expenses")
    public String viewExpenses(Model model,
                               @SessionAttribute("user") User user) {
        model.addAttribute("currency", user.getCurrency() != null ? user.getCurrency() : "$");
        model.addAttribute("list", service.getExpenses(user));
        return "expense-list";
    }
    @PostMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        service.deleteById(id);
        return "redirect:/expenses";
    }
}