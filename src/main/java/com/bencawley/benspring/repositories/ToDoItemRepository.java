package com.bencawley.benspring.repositories;

import com.bencawley.benspring.entities.ToDoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ToDoItemRepository extends JpaRepository<ToDoItemEntity, Long> {
    List<ToDoItemEntity> findByToDoListId(Long listId);
}
