package com.ghlabs.snippez.repository;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query(value = "SELECT new com.ghlabs.snippez.dto.CategoryDTO(c.id, c.name, c.snippets.size, c.icon, c.createdAt, c.updatedAt) FROM Category c " +
            "WHERE c.creator.id = :userId ")
    List<CategoryDTO> findCategoriesOfUser(@Param("userId") Long userId);
}
