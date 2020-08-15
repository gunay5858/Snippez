package com.ghlabs.snippez.repository;

import com.ghlabs.snippez.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    @Query("SELECT c FROM Category c")
    public ArrayList<Object> findAllCategories();
}
