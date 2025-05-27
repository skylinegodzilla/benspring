package com.bencawley.benspring.repositories;

import com.bencawley.benspring.entities.ToDoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoItemRepository extends JpaRepository<ToDoItemEntity, Long> {
}
