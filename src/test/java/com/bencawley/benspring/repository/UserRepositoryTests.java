package com.bencawley.benspring.repository;

import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepo;

    private UserEntity user;

    @BeforeEach
    void setup() {
        // given: a user entity saved in repo
        user = new UserEntity();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPasswordHash("$2a$10$fakehash");
        user.setSessionToken("token123");
        user = userRepo.save(user);
    }

    @Test
    void givenUserSaved_whenFindById_thenUserReturned() {
        // when
        UserEntity found = userRepo.findById(user.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void givenUserSaved_whenFindByUsername_thenUserReturned() {
        // when
        UserEntity found = userRepo.findByUsername("johndoe");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void givenUserSaved_whenFindBySessionToken_thenUserReturned() {
        // when
        UserEntity found = userRepo.findBySessionToken("token123");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void givenUserSaved_whenExistsByUsername_thenTrue() {
        // when
        boolean exists = userRepo.existsByUsername("johndoe");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void givenUserSaved_whenExistsByEmail_thenTrue() {
        // when
        boolean exists = userRepo.existsByEmail("john@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void givenUserSaved_whenUpdateAndSave_thenChangesPersisted() {
        // given
        user.setEmail("john.doe@newmail.com");

        // when
        userRepo.save(user);
        UserEntity updated = userRepo.findById(user.getId()).orElse(null);

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.getEmail()).isEqualTo("john.doe@newmail.com");
    }
}
