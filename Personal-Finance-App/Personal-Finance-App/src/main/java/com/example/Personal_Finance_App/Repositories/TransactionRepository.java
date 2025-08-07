package com.example.Personal_Finance_App.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Personal_Finance_App.Model.Transaction;
import com.example.Personal_Finance_App.Model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUser(User userId);

    List<Transaction> findByUserIdAndType(Long userId, String type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'INCOME'")
    Double getTotalIncome(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'EXPENSE'")
    Double getTotalExpense(@Param("userId") Long userId);

    default Double getSavings(Long userId) {
        Double income = getTotalIncome(userId);
        Double expense = getTotalExpense(userId);
        if (income == null)
            income = 0.0;
        if (expense == null)
            expense = 0.0;
        return income - expense;
    }

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'INCOME' AND FUNCTION('MONTH', t.date) = :month")
    Double getMonthlyIncomeByMonth(@Param("userId") Long userId, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND FUNCTION('MONTH', t.date) = :month")
    Double getMonthlyExpenseByMonth(@Param("userId") Long userId, @Param("month") int month);

}