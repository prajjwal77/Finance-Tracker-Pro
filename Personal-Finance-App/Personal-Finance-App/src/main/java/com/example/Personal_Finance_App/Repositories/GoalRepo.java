package com.example.Personal_Finance_App.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Personal_Finance_App.Model.Goal;

@Repository
public interface GoalRepo extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
}
