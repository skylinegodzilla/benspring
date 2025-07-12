package com.bencawley.benspring.repositories;

import com.bencawley.benspring.entities.ToDoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoItemRepository extends JpaRepository<ToDoItemEntity, Long> {
    List<ToDoItemEntity> findByList_IdOrderByPosition(Long listId);
    // this is basicley saying SELECT * FROM todo_item WHERE list_id = ?

// ─────────────────────────────────────────────────────────────────────────────
// Why do we need @Query here, but not for findByList_Id(Long listId)?
//
// The method `findByList_Id(Long listId)` written above is a *derived query* — Spring Data JPA
// understands the method name and automatically generates the appropriate SQL:
//   SELECT * FROM todo_items WHERE list_id = ?
//
// But the method below uses a custom JPQL query to find the *maximum* position
// of items within a list:
//
//   @Query("SELECT MAX(i.position) FROM ToDoItemEntity i WHERE i.list.id = :listId")
//   Optional<Integer> findMaxPositionByListId(@Param("listId") Long listId);
//
// This cannot be auto-generated because:
//   1. It's an *aggregation query* (uses MAX()),
//   2. It returns a *single scalar value* (Integer) rather than an entity,
//   3. It doesn’t match any known Spring Data method naming patterns.
//
// By using `@Query` with a named parameter (`:listId`), Spring still prepares the
// statement safely — it's secure and acts like a parameterized (prepared) query.
//
// TL;DR:
//  - Simple entity lookups = method name is enough
//  - Custom selects, aggregates, partial results = need @Query
// ─────────────────────────────────────────────────────────────────────────────

    // this is a prepared statement
    @Query("SELECT MAX(i.position) FROM ToDoItemEntity i WHERE i.list.id = :listId")
    Optional<Integer> findMaxPositionByListId(@Param("listId") Long listId);
}

