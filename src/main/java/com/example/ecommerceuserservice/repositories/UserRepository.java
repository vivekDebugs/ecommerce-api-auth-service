package com.example.ecommerceuserservice.repositories;

import com.example.ecommerceuserservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    User save(User user);

    Optional<User> findByEmail(String email);
}
