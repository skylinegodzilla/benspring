package com.bencawley.benspring.repository;

import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.repositories.ToDoItemRepository;
import com.bencawley.benspring.repositories.ToDoListRepository;
import com.bencawley.benspring.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ToDoItemRepositoryTests {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ToDoListRepository listRepo;

    @Autowired
    private ToDoItemRepository itemRepo;

    private ToDoListEntity list;

    @BeforeEach
    void setup() {
        // create user
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("$2a$10$ThisISaFakeHash");
        user = userRepo.save(user);

        // create list
        list = new ToDoListEntity();
        list.setTitle("Groceries");
        list.setDescription("Weekly stuff");
        list.setUser(user);
        list = listRepo.save(list);
    }

    @Test
    void givenToDoItem_whenSaved_thenFindByListIdReturnsSavedItems() {
        // given
        ToDoItemEntity item = new ToDoItemEntity();
        item.setTitle("Buy milk");
        item.setDescription("2L whole milk");
        item.setDueDate(LocalDate.now().plusDays(1));
        item.setList(list);
        itemRepo.save(item);

        // when
        List<ToDoItemEntity> items = itemRepo.findByList_IdOrderByPosition(list.getId());

        // then
        assertThat(items).hasSize(1)
                .extracting(ToDoItemEntity::getTitle)
                .containsExactly("Buy milk");
    }

    @Test
    void givenListWithItems_whenDeleted_thenItemsAreAlsoDeleted() {
        // given
        ToDoItemEntity item = new ToDoItemEntity();
        item.setTitle("Eggs");
        item.setDescription("Dozen free-range");
        item.setList(list);
        list.getItems().add(item); // important for cascade

        listRepo.save(list); // cascades save to items

        // when
        listRepo.delete(list); // cascades delete to items

        // then
        List<ToDoItemEntity> items = itemRepo.findByList_IdOrderByPosition(list.getId());
        assertThat(items).isEmpty();
    }

    @Test
    void givenSavedItem_whenTitleUpdated_thenChangeIsPersisted() {
        // given
        ToDoItemEntity item = new ToDoItemEntity();
        item.setTitle("Buy milk");
        item.setDescription("2L whole milk");
        item.setDueDate(LocalDate.now().plusDays(1));
        item.setList(list);
        item = itemRepo.save(item);

        // when
        item.setTitle("Buy almond milk");
        itemRepo.save(item); // save the updated entity

        // then
        ToDoItemEntity updatedItem = itemRepo.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getTitle()).isEqualTo("Buy almond milk");
    }
}
