package com.bencawley.benspring.repositories;

import com.bencawley.benspring.entities.ToDoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ToDoItemRepository extends JpaRepository<ToDoItemEntity, Long> {
    List<ToDoItemEntity> findByList_Id(Long listId);
    // this is basicley saying SELECT * FROM todo_item WHERE list_id = ?
}

