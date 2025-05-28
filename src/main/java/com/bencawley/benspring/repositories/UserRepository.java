package com.bencawley.benspring.repositories;

import com.bencawley.benspring.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// this is an interface. Its just Javas version of protocols
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    UserEntity findBySessionToken(String sessionToken);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}