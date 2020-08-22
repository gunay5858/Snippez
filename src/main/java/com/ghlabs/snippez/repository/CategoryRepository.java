package com.ghlabs.snippez.repository;

import com.ghlabs.snippez.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    @Query("SELECT c FROM Category c")
    public ArrayList<Category> findAllCategories();

    @Query("SELECT c FROM Category c WHERE c.creator.id = :userId")
    public ArrayList<Category> findCategoriesOfUser(@Param("userId") Long userId);
}
