package com.example.Personal_Finance_App.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Personal_Finance_App.Model.Savings;


@Repository
public interface SavingRepo extends JpaRepository<Savings, Long> {
    List<Savings> findByUserId(Long userId);
}
