package com.example.expensetracker.ExpenseTracker.login.controller;


import com.example.expensetracker.ExpenseTracker.login.model.Expense;
import com.example.expensetracker.ExpenseTracker.login.model.Income;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.service.ExpenseService;
import com.example.expensetracker.ExpenseTracker.login.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SavingsController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private IncomeService incomeService;

    @GetMapping("/savings")
    public String showSavings(Model model,
                              @SessionAttribute("user") User user) {

        List<Expense> allExpenses = expenseService.getExpenses(user);
        List<Income>  allIncomes  = incomeService.getIncome(user);

        // ── Overall totals ──
        double totalExpense = allExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        double totalIncome = allIncomes.stream()
                .mapToDouble(Income::getAmount).sum();

        double netSavings  = totalIncome - totalExpense;
        double savingsRate = totalIncome > 0 ? (netSavings / totalIncome) * 100 : 0;
        boolean isSaving   = netSavings >= 0;

        // ── Monthly breakdown ──
        // Collect all distinct month keys from both lists
        Set<String> allMonthKeys = new LinkedHashSet<>();

        allIncomes.stream()
                .filter(i -> i.getDate() != null)
                .sorted(Comparator.comparing(Income::getDate).reversed())
                .forEach(i -> allMonthKeys.add(
                        i.getDate().getYear() + "-" +
                                String.format("%02d", i.getDate().getMonthValue())));

        allExpenses.stream()
                .filter(e -> e.getDate() != null)
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .forEach(e -> allMonthKeys.add(
                        e.getDate().getYear() + "-" +
                                String.format("%02d", e.getDate().getMonthValue())));

        // Sort keys descending (most recent first)
        List<String> sortedKeys = new ArrayList<>(allMonthKeys);
        Collections.sort(sortedKeys, Collections.reverseOrder());

        List<Map<String, Object>> monthlySavings = new ArrayList<>();

        for (String key : sortedKeys) {
            String[] parts = key.split("-");
            int year  = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            double monthIncome = allIncomes.stream()
                    .filter(i -> i.getDate() != null
                            && i.getDate().getYear() == year
                            && i.getDate().getMonthValue() == month)
                    .mapToDouble(Income::getAmount).sum();

            double monthExpense = allExpenses.stream()
                    .filter(e -> e.getDate() != null
                            && e.getDate().getYear() == year
                            && e.getDate().getMonthValue() == month)
                    .mapToDouble(Expense::getAmount).sum();

            double monthNet = monthIncome - monthExpense;

            // For bar chart percentages
            double maxVal = Math.max(monthIncome, monthExpense);
            int incomePct  = maxVal > 0 ? (int) Math.round((monthIncome  / maxVal) * 100) : 0;
            int expensePct = maxVal > 0 ? (int) Math.round((monthExpense / maxVal) * 100) : 0;

            // Month display name
            String monthName = java.time.Month.of(month)
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("monthName",   monthName);
            row.put("income",      String.format("%.2f", monthIncome));
            row.put("expense",     String.format("%.2f", monthExpense));
            row.put("savings",     String.format("%.2f", monthNet));
            row.put("isSaving",    monthNet >= 0);
            row.put("incomePct",   incomePct);
            row.put("expensePct",  expensePct);

            monthlySavings.add(row);
        }

        // ── Add to model ──
        model.addAttribute("totalIncome",    String.format("%.2f", totalIncome));
        model.addAttribute("totalExpense",   String.format("%.2f", totalExpense));
        model.addAttribute("netSavings",     String.format("%.2f", Math.abs(netSavings)));
        model.addAttribute("savingsRate",    String.format("%.1f", Math.abs(savingsRate)));
        model.addAttribute("isSaving",       isSaving);
        model.addAttribute("monthlySavings", monthlySavings);
        model.addAttribute("currency", user.getCurrency() != null ? user.getCurrency() : "$"); // ← only here


        return "savings";
    }
}