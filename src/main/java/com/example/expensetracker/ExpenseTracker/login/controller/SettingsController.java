package com.example.expensetracker.ExpenseTracker.login.controller;


import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.service.ExpenseService;
import com.example.expensetracker.ExpenseTracker.login.service.IncomeService;
import com.example.expensetracker.ExpenseTracker.login.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SettingsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private IncomeService incomeService;

    // ── Show Settings Page ──
    @GetMapping("/settings")
    public String showSettings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";

        model.addAttribute("user", user);
        model.addAttribute("currency", user.getCurrency() != null ? user.getCurrency() : "$");
        return "settings";
    }

    // ── Update Name ──
    @PostMapping("/settings/name")
    public String updateName(@RequestParam String newName,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";

        userService.updateName(user, newName);
        session.setAttribute("user", user);
        return "redirect:/settings?success=name";
    }

    // ── Update Password ──
    @PostMapping("/settings/profile")
    public String updateProfile(@RequestParam(required = false) String newName,
                                @RequestParam(required = false) String currentPassword,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) String confirmPassword,
                                HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";

        // Update name if provided
        if (newName != null && !newName.isBlank()) {
            userService.updateName(user, newName);
        }

        // Update password if provided
        if (currentPassword != null && !currentPassword.isBlank()) {
            if (!user.getPassword().equals(currentPassword))
                return "redirect:/settings?error=wrongpassword";
            if (!newPassword.equals(confirmPassword))
                return "redirect:/settings?error=mismatch";
            userService.updatePassword(user, newPassword);
        }

        session.setAttribute("user", user);
        return "redirect:/settings?success=profile";
    }
    // ── Update Email ──
    @PostMapping("/settings/email")
    public String updateEmail(@RequestParam String newEmail,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";

        userService.updateEmail(user, newEmail);
        session.setAttribute("user", user);
        return "redirect:/settings?success=email";
    }
    // ── Update Currency ──
    @PostMapping("/settings/currency")
    public String updateCurrency(@RequestParam String currency,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";

        userService.updateCurrency(user, currency);
        session.setAttribute("user", user);
        return "redirect:/settings?success=currency";
    }

    // ── Delete All Expenses ──
    @PostMapping("/settings/delete-expenses")
    public String deleteExpenses(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";

        expenseService.deleteAllByUser(user);
        return "redirect:/settings?success=expenses";
    }

    // ── Delete All Income ──
    @PostMapping("/settings/delete-income")
    public String deleteIncome(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";

        incomeService.deleteAllByUser(user);
        return "redirect:/settings?success=income";
    }
}
