package com.example.Personal_Finance_App.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Personal_Finance_App.Model.Budget;

@Repository
public interface BudgetRepo extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
}
