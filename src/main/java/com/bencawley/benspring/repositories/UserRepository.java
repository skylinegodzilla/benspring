package com.bencawley.benspring.repositories;

import com.bencawley.benspring.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    UserEntity findBySessionToken(String sessionToken);
}