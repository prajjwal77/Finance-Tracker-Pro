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

            if (title.matches(
                    ".*\\b(grocery|food|restaurant|meal|snack|cafe|dinner|lunch|breakfast|pizza|burger)\\b.*")) {
                transaction.setCategory("Food");
            } else if (title.matches(".*\\b(rent|apartment|house|mortgage|lease|hostel)\\b.*")) {
                transaction.setCategory("Housing");
            } else if (title.matches(".*\\b(bus|fuel|train|taxi|uber|ola|petrol|diesel|transport|cab|metro)\\b.*")) {
                transaction.setCategory("Transport");
            } else if (title
                    .matches(".*\\b(electricity|water|internet|gas|utility|utilities|wifi|broadband|recharge)\\b.*")) {
                transaction.setCategory("Utilities");
            } else if (title
                    .matches(".*\\b(movie|netflix|prime|game|entertainment|spotify|theatre|music|concert)\\b.*")) {
                transaction.setCategory("Entertainment");
            } else if (title
                    .matches(".*\\b(medicine|hospital|doctor|pharmacy|health|clinic|treatment|insurance)\\b.*")) {
                transaction.setCategory("Healthcare");
            } else if (title.matches(
                    ".*\\b(school|college|tuition|education|course|exam|fee|library|university|learning)\\b.*")) {
                transaction.setCategory("Education");
            } else if (title.matches(
                    ".*\\b(clothes|shopping|mall|shoes|fashion|apparel|dress|jeans|tshirt|zara|h&m|store)\\b.*")) {
                transaction.setCategory("Shopping");
            } else if (title.matches(".*\\b(travel|trip|flight|hotel|vacation|holiday|airbnb|stay|tour)\\b.*")) {
                transaction.setCategory("Travel");
            } else if (title.matches(".*\\b(saving|deposit|investment|stock|mutual|fund|sip|bank|interest)\\b.*")) {
                transaction.setCategory("Savings & Investments");
            } else {
                transaction.setCategory("Other");
            }
        }
        transactionRepository.save(transaction);
        return "redirect:/dashboard";
    }
}