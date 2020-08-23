package com.ghlabs.snippez.service;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.repository.CategoryRepository;
import com.ghlabs.snippez.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public CategoryService(@Autowired CategoryRepository categoryRepository, @Autowired UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryDTO> findAllCategories() {
        return ((List<Category>) categoryRepository
                .findAll())
                .stream()
                .map(this::convertToCategoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO addCategory(Category category) {
        UserDTO creator = null;
        try {
            creator = modelMapper.map(userRepository.findById(category.getCreator().getId()).get(), UserDTO.class);
        } catch (NoSuchElementException e) {
            return null;
        }

        category.setCreator(modelMapper.map(creator, User.class));
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    public CategoryDTO findCategoryById(Long catId) {
        if (categoryRepository.findById(catId).isPresent()) {
            return modelMapper.map(categoryRepository.findById(catId).get(), CategoryDTO.class);
        } else {
            return null;
        }
    }

    public CategoryDTO updateCategory(Long id, Category category) {
        if (categoryRepository.findById(id).isPresent()) {
            Category dbCategory = categoryRepository.findById(id).get();
            dbCategory.setId(id);

            // encrypt password if new one is set
            if (category.getName() != null) {
                dbCategory.setName(category.getName());
            }

            if (category.getIcon() != null) {
                dbCategory.setIcon(category.getIcon());
            }

            return modelMapper.map(categoryRepository.save(dbCategory), CategoryDTO.class);
        } else {
            return null;
        }
    }

    public void deleteCategoryById(Long catId) {
        categoryRepository.deleteById(catId);
    }


    public List<CategoryDTO> findCategoriesOfUser(Long userId) {
        return ((List<Category>) categoryRepository
                .findCategoriesOfUser(userId))
                .stream()
                .map(this::convertToCategoryDTO)
                .collect(Collectors.toList());
    }

    private CategoryDTO convertToCategoryDTO(Category category) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper
                .map(category, CategoryDTO.class);
    }
}
