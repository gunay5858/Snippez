package com.ghlabs.snippez.service;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.repository.CategoryRepository;
import com.ghlabs.snippez.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        //return categoryRepository.findAllCategories();
        return ((List<Category>) categoryRepository
                .findAll())
                .stream()
                .map(this::convertToCategoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO addCategory(Category category) {
        System.out.println("++++++++++ " + category.getCreator().toString());
        User creator = userRepository.findById(category.getCreator().getId()).get();
        category.setCreator(modelMapper.map(creator, User.class));
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    private CategoryDTO convertToCategoryDTO(Category category) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper
                .map(category, CategoryDTO.class);
    }
}
