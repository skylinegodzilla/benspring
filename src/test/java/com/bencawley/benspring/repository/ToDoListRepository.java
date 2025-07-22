package com.bencawley.benspring.repository;

import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.ToDoListRepository;
import com.bencawley.benspring.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ToDoListRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ToDoListRepository listRepo;

    private UserEntity user;

    @BeforeEach
    void setup() {
        // given: a user saved to repo
        user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("$2a$10$ThisIsAFakeHash");
        user = userRepo.save(user);
    }

    @Test
    void givenUserWithNoLists_whenFindByUserId_thenReturnEmptyList() {
        // when
        List<ToDoListEntity> lists = listRepo.findByUser_Id(user.getId());

        // then
        assertThat(lists).isEmpty();
    }

    @Test
    void givenSavedLists_whenFindByUserId_thenReturnLists() {
        // given: two lists belonging to the same user
        ToDoListEntity list1 = new ToDoListEntity();
        list1.setTitle("Groceries");
        list1.setDescription("Weekly groceries");
        list1.setUser(user);
        listRepo.save(list1);

        ToDoListEntity list2 = new ToDoListEntity();
        list2.setTitle("Work Tasks");
        list2.setDescription("Tasks for work");
        list2.setUser(user);
        listRepo.save(list2);

        // when
        List<ToDoListEntity> lists = listRepo.findByUser_Id(user.getId());

        // then
        assertThat(lists).hasSize(2)
                .extracting(ToDoListEntity::getTitle)
                .containsExactlyInAnyOrder("Groceries", "Work Tasks");
    }

    @Test
    void givenSavedList_whenTitleUpdated_thenChangeIsPersisted() {
        // given
        ToDoListEntity savedList = new ToDoListEntity();
        savedList.setTitle("Old title");
        savedList.setDescription("Some description");
        savedList.setUser(user);
        savedList = listRepo.save(savedList);

        // when
        savedList.setTitle("New title");
        listRepo.save(savedList);

        // then
        ToDoListEntity updatedList = listRepo.findById(savedList.getId()).orElseThrow();
        assertThat(updatedList.getTitle()).isEqualTo("New title");
    }

    @Test
    void givenSavedList_whenDeleted_thenIsRemoved() {
        // given
        ToDoListEntity savedList = new ToDoListEntity();
        savedList.setTitle("Temporary list");
        savedList.setUser(user);
        savedList = listRepo.save(savedList);

        // when
        listRepo.delete(savedList);

        // then
        boolean exists = listRepo.existsById(savedList.getId());
        assertThat(exists).isFalse();
    }
}
