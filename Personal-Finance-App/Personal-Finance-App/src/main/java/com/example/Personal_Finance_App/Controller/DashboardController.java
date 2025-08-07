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

import com.example.Personal_Finance_App.Model.Transaction;
import com.example.Personal_Finance_App.Model.User;
import com.example.Personal_Finance_App.Repositories.BudgetRepo;
import com.example.Personal_Finance_App.Repositories.GoalRepo;
import com.example.Personal_Finance_App.Repositories.SavingRepo;
import com.example.Personal_Finance_App.Repositories.TransactionRepository;
import com.example.Personal_Finance_App.Repositories.UserRepository;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private TransactionRepository transactionRepo;
    @Autowired
    private BudgetRepo budgetRepo;
    @Autowired
    private SavingRepo savingsRepo;
    @Autowired
    private GoalRepo goalRepo;

    @GetMapping("/dashboard")
public String dashboard(Model model, Principal principal) {
    String email = principal.getName();
    User user = userRepo.findByEmail(email);
    if (user == null) return "redirect:/login";

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
    if (savings == null) savings = 0.0;

    // Add to model
    model.addAttribute("transactions", transactions);
    model.addAttribute("totalIncome", totalIncome);
    model.addAttribute("totalExpense", totalExpense);
    model.addAttribute("balance", balance);
    model.addAttribute("savings", savings);
    model.addAttribute("categoryTotals", categoryTotals);
    model.addAttribute("monthlyIncome", monthlyIncome);
    model.addAttribute("monthlyExpenses", monthlyExpenses);
    model.addAttribute("monthlySavings", monthlySavings);

    return "dashboard";
}

}