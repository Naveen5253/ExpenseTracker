package com.example.expensetracker.ExpenseTracker.login.controller;


import com.example.expensetracker.ExpenseTracker.login.model.Expense;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReportsController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/reports")
    public String showReports(Model model,
                              @SessionAttribute("user") User user) {

        List<Expense> allExpenses = expenseService.getExpenses(user);

        // ── Summary stats ──
        double totalSpent = allExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        int totalEntries = allExpenses.size();

        double highestExpense = allExpenses.stream()
                .mapToDouble(Expense::getAmount).max().orElse(0);

        // Distinct months that have expenses
        long distinctMonths = allExpenses.stream()
                .filter(e -> e.getDate() != null)
                .map(e -> e.getDate().getYear() + "-" + e.getDate().getMonthValue())
                .distinct().count();

        double avgPerMonth = distinctMonths > 0 ? totalSpent / distinctMonths : 0;

        // ── Category report ──
        Map<String, List<Expense>> byCategory = allExpenses.stream()
                .filter(e -> e.getCategory() != null)
                .collect(Collectors.groupingBy(Expense::getCategory));

        List<Map<String, Object>> categoryReport = new ArrayList<>();

        for (Map.Entry<String, List<Expense>> entry : byCategory.entrySet()) {
            double catTotal = entry.getValue().stream()
                    .mapToDouble(Expense::getAmount).sum();
            int catCount = entry.getValue().size();
            double percentage = totalSpent > 0 ? (catTotal / totalSpent) * 100 : 0;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("category",   entry.getKey());
            row.put("total",      String.format("%.2f", catTotal));
            row.put("count",      catCount);
            row.put("percentage", String.format("%.1f", percentage));
            row.put("barWidth",   (int) Math.round(percentage));
            categoryReport.add(row);
        }

        // Sort by total descending
        categoryReport.sort((a, b) -> {
            double av = Double.parseDouble((String) a.get("total"));
            double bv = Double.parseDouble((String) b.get("total"));
            return Double.compare(bv, av);
        });

        // ── Monthly report ──
        Map<String, List<Expense>> byMonth = allExpenses.stream()
                .filter(e -> e.getDate() != null)
                .collect(Collectors.groupingBy(e ->
                        e.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                + " " + e.getDate().getYear()));

        List<Map<String, Object>> monthlyReport = new ArrayList<>();

        for (Map.Entry<String, List<Expense>> entry : byMonth.entrySet()) {
            double monthTotal = entry.getValue().stream()
                    .mapToDouble(Expense::getAmount).sum();
            int monthCount = entry.getValue().size();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month",   entry.getKey());
            row.put("total",   String.format("%.2f", monthTotal));
            row.put("count",   monthCount);
            monthlyReport.add(row);
        }

        // Sort by total descending
        monthlyReport.sort((a, b) -> {
            double av = Double.parseDouble((String) a.get("total"));
            double bv = Double.parseDouble((String) b.get("total"));
            return Double.compare(bv, av);
        });

        // ── Add to model ──
        model.addAttribute("totalSpent",      String.format("%.2f", totalSpent));
        model.addAttribute("totalEntries",    totalEntries);
        model.addAttribute("highestExpense",  String.format("%.2f", highestExpense));
        model.addAttribute("avgPerMonth",     String.format("%.2f", avgPerMonth));
        model.addAttribute("categoryReport",  categoryReport);
        model.addAttribute("monthlyReport",   monthlyReport);
        model.addAttribute("allExpenses",     allExpenses);
        model.addAttribute("currency", user.getCurrency() != null ? user.getCurrency() : "$"); // ← only here


        return "reports";
    }
}
