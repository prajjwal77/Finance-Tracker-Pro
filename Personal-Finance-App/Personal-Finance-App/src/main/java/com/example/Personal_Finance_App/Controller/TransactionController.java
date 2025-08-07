package com.example.Personal_Finance_App.Controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Personal_Finance_App.Model.Transaction;
import com.example.Personal_Finance_App.Model.User;
import com.example.Personal_Finance_App.Repositories.TransactionRepository;
import com.example.Personal_Finance_App.Repositories.UserRepository;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/income/add")
    public String showAddIncomeForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "add-income"; // Thymeleaf view name
    }

    @PostMapping("/income/save")
    public String saveIncome(@ModelAttribute("transaction") Transaction transaction, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "redirect:/login"; // fail-safe
        }

        transaction.setUser(user);
        transaction.setType("INCOME");
        transaction.setDate(LocalDate.now());

        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            transaction.setDescription(transaction.getTitle());
        }

        transactionRepository.save(transaction);

        return "redirect:/dashboard";
    }

    // add expenses method
    @GetMapping("/expense/add")
    public String showAddExpenseForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "add-expense"; // Thymeleaf view name
    }
    @PostMapping("/expense/save")
    public String saveExpense(@ModelAttribute("transaction") Transaction transaction, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "redirect:/login"; // fail-safe
        }

        transaction.setUser(user);
        transaction.setType("EXPENSE");
        transaction.setDate(LocalDate.now());

        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            transaction.setDescription(transaction.getTitle());
        }

        // ✅ Auto-categorize if category is missing or blank
        if (transaction.getCategory() == null || transaction.getCategory().trim().isEmpty()) {
            String title = transaction.getTitle().toLowerCase();
            if (title.contains("grocery") || title.contains("food") || title.contains("restaurant")) {
                transaction.setCategory("Food");
            } else if (title.contains("rent") || title.contains("apartment") || title.contains("house")) {
                transaction.setCategory("Housing");
            } else if (title.contains("bus") || title.contains("fuel") || title.contains("train")
                    || title.contains("taxi")) {
                transaction.setCategory("Transport");
            } else if (title.contains("electricity") || title.contains("water") || title.contains("internet")) {
                transaction.setCategory("Utilities");
            } else if (title.contains("movie") || title.contains("netflix") || title.contains("game")) {
                transaction.setCategory("Entertainment");
            } else {
                transaction.setCategory("Other");
            }
        }
        transactionRepository.save(transaction);
        return "redirect:/dashboard";
    }
}