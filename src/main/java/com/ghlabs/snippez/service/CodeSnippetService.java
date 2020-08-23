package com.ghlabs.snippez.service;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.dto.CodeSnippetDTO;
import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.entity.CodeSnippet;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.repository.CategoryRepository;
import com.ghlabs.snippez.repository.CodeSnippetRepository;
import com.ghlabs.snippez.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CodeSnippetService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final CodeSnippetRepository codeSnippetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CodeSnippetService(@Autowired CodeSnippetRepository codeSnippetRepository, @Autowired UserRepository userRepository, @Autowired CategoryRepository categoryRepository) {
        this.codeSnippetRepository = codeSnippetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CodeSnippetDTO> findAllCodeSnippets() {
        return ((List<CodeSnippet>) codeSnippetRepository
                .findAll())
                .stream()
                .map(this::convertToCodeSnippetDTO)
                .collect(Collectors.toList());
    }

    public CodeSnippetDTO addCodeSnippet(CodeSnippet codeSnippet) {
        UserDTO creator = null;
        try {
            creator = modelMapper.map(userRepository.findById(codeSnippet.getCreator().getId()).get(), UserDTO.class);
        } catch (NoSuchElementException e) {
            return null;
        }

        try {
            CategoryDTO categoryDTO = modelMapper.map(categoryRepository.findById(codeSnippet.getCategory().getId()).get(), CategoryDTO.class);
            codeSnippet.setCategory(modelMapper.map(categoryDTO, Category.class));
        } catch (NullPointerException e) {
            codeSnippet.setCategory(null);
        }

        codeSnippet.setCreator(modelMapper.map(creator, User.class));
        return modelMapper.map(codeSnippetRepository.save(codeSnippet), CodeSnippetDTO.class);
    }

    public CodeSnippetDTO findById(Long id) {
        if (codeSnippetRepository.findById(id).isPresent()) {
            return modelMapper.map(codeSnippetRepository.findById(id).get(), CodeSnippetDTO.class);
        } else {
            return null;
        }
    }

    public void deleteCodeSnippetById(Long id) {
        codeSnippetRepository.deleteById(id);
    }

    private CodeSnippetDTO convertToCodeSnippetDTO(CodeSnippet codeSnippet) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper
                .map(codeSnippet, CodeSnippetDTO.class);
    }
}
