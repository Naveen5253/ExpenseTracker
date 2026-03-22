package com.example.expensetracker.ExpenseTracker.login.controller;

import com.example.expensetracker.ExpenseTracker.login.model.Income;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.service.ExpenseService;
import com.example.expensetracker.ExpenseTracker.login.service.IncomeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @GetMapping("/add-income")
    public String showIncomeForm(Model model) {
        model.addAttribute("income", new Income());
        return "add-income";
    }

    @PostMapping("/income/add")
    public String addIncome(@RequestParam Double amount,
                            @RequestParam String date,
                            @SessionAttribute("user") User user) {
        Income income = new Income();
        income.setAmount(amount);
        income.setDate(LocalDate.parse(date));
        income.setUser(user);
        incomeService.saveIncome(income);
        return "redirect:/income";
    }

    @GetMapping("/income")
    public String viewIncome(Model model,
                             @SessionAttribute("user") User user) {
        model.addAttribute("list", incomeService.getIncome(user));
        model.addAttribute("currency", user.getCurrency() != null ? user.getCurrency() : "$");

        return "income-list";
    }
    @PostMapping("/income/delete/{id}")
    public String deleteIncome(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        incomeService.deleteById(id);
        return "redirect:/income";
    }

}
