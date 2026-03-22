package com.example.expensetracker.ExpenseTracker.login.controller;


import com.example.expensetracker.ExpenseTracker.login.model.Expense;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ComparisonController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/comparison")
    public String showComparison(Model model,
                                 @SessionAttribute("user") User user) {

        List<Expense> allExpenses = expenseService.getExpenses(user);

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear  = now.getYear();

        LocalDate lastMonthDate = now.minusMonths(1);
        int lastMonth     = lastMonthDate.getMonthValue();
        int lastMonthYear = lastMonthDate.getYear();

        String currentMonthName = now.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + currentYear;
        String lastMonthName = lastMonthDate.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + lastMonthYear;

        List<Expense> currentMonthExpenses = allExpenses.stream()
                .filter(e -> e.getDate() != null
                        && e.getDate().getMonthValue() == currentMonth
                        && e.getDate().getYear() == currentYear)
                .collect(Collectors.toList());

        List<Expense> lastMonthExpenses = allExpenses.stream()
                .filter(e -> e.getDate() != null
                        && e.getDate().getMonthValue() == lastMonth
                        && e.getDate().getYear() == lastMonthYear)
                .collect(Collectors.toList());

        // Use mapToDouble with method reference — works for both double and Double
        double currentTotal = currentMonthExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        double lastTotal = lastMonthExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        double difference = currentTotal - lastTotal;

        Set<String> allCategories = new HashSet<>();
        currentMonthExpenses.forEach(e -> {
            if (e.getCategory() != null) allCategories.add(e.getCategory());
        });
        lastMonthExpenses.forEach(e -> {
            if (e.getCategory() != null) allCategories.add(e.getCategory());
        });

        double maxAmount = allCategories.stream().mapToDouble(cat -> {
            double cur = currentMonthExpenses.stream()
                    .filter(e -> cat.equals(e.getCategory()))
                    .mapToDouble(Expense::getAmount).sum();
            double last = lastMonthExpenses.stream()
                    .filter(e -> cat.equals(e.getCategory()))
                    .mapToDouble(Expense::getAmount).sum();
            return Math.max(cur, last);
        }).max().orElse(1);

        List<Map<String, Object>> categoryComparison = new ArrayList<>();

        for (String cat : allCategories) {
            double curAmt = currentMonthExpenses.stream()
                    .filter(e -> cat.equals(e.getCategory()))
                    .mapToDouble(Expense::getAmount).sum();

            double lastAmt = lastMonthExpenses.stream()
                    .filter(e -> cat.equals(e.getCategory()))
                    .mapToDouble(Expense::getAmount).sum();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("category",      cat);
            row.put("currentAmount", String.format("%.2f", curAmt));
            row.put("lastAmount",    String.format("%.2f", lastAmt));
            row.put("currentPct",    (int) Math.round((curAmt  / maxAmount) * 100));
            row.put("lastPct",       (int) Math.round((lastAmt / maxAmount) * 100));
            categoryComparison.add(row);
        }

        categoryComparison.sort((a, b) -> {
            double av = Double.parseDouble((String) a.get("currentAmount"));
            double bv = Double.parseDouble((String) b.get("currentAmount"));
            return Double.compare(bv, av);
        });

        model.addAttribute("currentMonthName",    currentMonthName);
        model.addAttribute("lastMonthName",        lastMonthName);
        model.addAttribute("currentMonthTotal",    String.format("%.2f", currentTotal));
        model.addAttribute("lastMonthTotal",       String.format("%.2f", lastTotal));
        model.addAttribute("difference",           difference);
        model.addAttribute("currentMonthExpenses", currentMonthExpenses);
        model.addAttribute("lastMonthExpenses",    lastMonthExpenses);
        model.addAttribute("categoryComparison",   categoryComparison);
        model.addAttribute("currency", user.getCurrency() != null ? user.getCurrency() : "$");


        return "comparison";
    }
}