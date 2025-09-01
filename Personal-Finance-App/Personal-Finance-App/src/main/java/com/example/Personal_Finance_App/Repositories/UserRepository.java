package com.example.Personal_Finance_App.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Personal_Finance_App.Model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
