package com.example.Personal_Finance_App.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Personal_Finance_App.Model.Transaction;
import com.example.Personal_Finance_App.Model.User;
import com.example.Personal_Finance_App.Repositories.TransactionRepository;
import com.example.Personal_Finance_App.Service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepo;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("user", new User());
        return "home";
    }

    @GetMapping("/newHome")
    public String newHome(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            List<Transaction> transactions = transactionRepo.findByUser(user);
            double totalIncome = 0.0;
            double totalExpense = 0.0;
            double balance;
            Map<String, Double> categoryTotals = new HashMap<>();
            Map<String, Double> monthlySavings = new LinkedHashMap<>();
            Map<String, Double> monthlyIncome = new LinkedHashMap<>();
            Map<String, Double> monthlyExpenses = new LinkedHashMap<>();

            // Prepare monthly data
            for (Transaction t : transactions) {
                String month = t.getDate().getMonth().toString().substring(0, 3); // e.g., "JAN"
                month = month.charAt(0) + month.substring(1).toLowerCase(); // e.g., "Jan"

                if ("INCOME".equalsIgnoreCase(t.getType())) {
                    totalIncome += t.getAmount();
                    monthlyIncome.put(month, monthlyIncome.getOrDefault(month, 0.0) + t.getAmount());
                } else if ("EXPENSE".equalsIgnoreCase(t.getType())) {
                    totalExpense += t.getAmount();
                    monthlyExpenses.put(month, monthlyExpenses.getOrDefault(month, 0.0) + t.getAmount());

                    String category = t.getCategory();
                    if (category != null) {
                        categoryTotals.put(category,
                                categoryTotals.getOrDefault(category, 0.0) + t.getAmount());
                    }
                }

                // Monthly savings = income - expenses
                double income = monthlyIncome.getOrDefault(month, 0.0);
                double expense = monthlyExpenses.getOrDefault(month, 0.0);
                monthlySavings.put(month, income - expense);
            }
            balance = totalIncome - totalExpense;
            Double savings = transactionRepo.getSavings(user.getId());
            if (savings == null)
                savings = 0.0;

            // Add to model
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("totalIncome", totalIncome);
                model.addAttribute("totalExpense", totalExpense);
            } else {
                model.addAttribute("user", new User()); // fallback
            }
        } else {
            model.addAttribute("user", new User()); // not logged in
        }

        return "index"; // will show logged-in user info in index.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        userService.saveUser(user); // save user
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}