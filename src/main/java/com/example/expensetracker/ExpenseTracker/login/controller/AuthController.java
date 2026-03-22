package com.example.expensetracker.ExpenseTracker.login.controller;



import com.example.expensetracker.ExpenseTracker.login.model.Expense;
import com.example.expensetracker.ExpenseTracker.login.model.Income;
import com.example.expensetracker.ExpenseTracker.login.model.User;
import com.example.expensetracker.ExpenseTracker.login.service.ExpenseService;
import com.example.expensetracker.ExpenseTracker.login.service.IncomeService;
import com.example.expensetracker.ExpenseTracker.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private IncomeService incomeService;

    @GetMapping("/")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        service.register(user);
        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            Model model,
                            HttpSession session) {
        User user = service.login(email, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid Credentials");

        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("name", user.getName());

        LocalDate now = LocalDate.now();
        List<Expense> allExpenses = expenseService.getExpenses(user);
        List<Income>  allIncomes  = incomeService.getIncome(user);

        double monthIncome = allIncomes.stream()
                .filter(i -> i.getDate() != null
                        && i.getDate().getMonthValue() == now.getMonthValue()
                        && i.getDate().getYear() == now.getYear())
                .mapToDouble(Income::getAmount).sum();

        double monthExpense = allExpenses.stream()
                .filter(e -> e.getDate() != null
                        && e.getDate().getMonthValue() == now.getMonthValue()
                        && e.getDate().getYear() == now.getYear())
                .mapToDouble(Expense::getAmount).sum();
        double totalIncome  = allIncomes.stream().mapToDouble(Income::getAmount).sum();
        double totalExpense = allExpenses.stream().mapToDouble(Expense::getAmount).sum();
        double totalSavings = totalIncome - totalExpense;

        model.addAttribute("totalSavings", String.format("%.2f", totalSavings));

        int totalReports = allExpenses.size();

        model.addAttribute("monthIncome",  String.format("%.2f", monthIncome));
        model.addAttribute("monthExpense", String.format("%.2f", monthExpense));
        model.addAttribute("totalReports", totalReports);
        model.addAttribute("currency", user.getCurrency() != null ? user.getCurrency() : "$");

        return "dashboard";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}