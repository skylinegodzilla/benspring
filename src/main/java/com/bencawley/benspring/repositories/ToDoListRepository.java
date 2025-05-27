package com.bencawley.benspring.repositories;

import com.bencawley.benspring.entities.ToDoListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoListRepository extends JpaRepository<ToDoListEntity, Long> {
    List<ToDoListEntity> findByUserId(Long userId);
}