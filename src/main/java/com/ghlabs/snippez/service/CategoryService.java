package com.ghlabs.snippez.service;

import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(@Autowired CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ArrayList<Object> findAllCategories() {
        return categoryRepository.findAllCategories();
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }
}
