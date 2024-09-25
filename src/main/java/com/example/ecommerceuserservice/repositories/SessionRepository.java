package com.example.ecommerceuserservice.repositories;

import com.example.ecommerceuserservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Override
    Session save(Session session);

    Boolean existsByTokenAndUserId(String token, Long userId);

    void deleteByToken(String token);
}
