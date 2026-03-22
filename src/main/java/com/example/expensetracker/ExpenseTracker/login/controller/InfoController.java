package com.example.expensetracker.ExpenseTracker.login.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InfoController {

    // ── About Us ──
    @GetMapping("/about")
    public String aboutPage(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/";
        return "about";
    }

    // ── Contact Us ──
    @GetMapping("/contact")
    public String contactPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/";
        return "contact";
    }

    // ── Handle Contact Form ──
    @PostMapping("/contact/send")
    public String sendMessage(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String subject,
                              @RequestParam String message,
                              HttpSession session) {

        if (session.getAttribute("user") == null) return "redirect:/";

        // TODO: plug in email service (JavaMailSender) here when ready
        // For now, just log and redirect with success
        System.out.println("=== New Contact Message ===");
        System.out.println("From    : " + name + " <" + email + ">");
        System.out.println("Subject : " + subject);
        System.out.println("Message : " + message);
        System.out.println("===========================");

        return "redirect:/contact?success=sent";
    }
}