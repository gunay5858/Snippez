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

import java.util.ArrayList;
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

    public List<CodeSnippetDTO> findAllCodeSnippetsOfCategory(Long categoryId) {
        return codeSnippetRepository
                .findCodeSnippetsOfCategory(categoryId)
                .stream()
                .map(this::convertToCodeSnippetDTO)
                .collect(Collectors.toList());
    }

    public CodeSnippetDTO addCodeSnippet(CodeSnippet codeSnippet) {
        UserDTO creator;
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

        if (codeSnippet.getSharedUsers() != null) {
            if (codeSnippet.getSharedUsers().size() > 0) {
                ArrayList<User> sharedUsers = new ArrayList<>();

                for (User u : codeSnippet.getSharedUsers()) {
                    sharedUsers.add(userRepository.findUserByUsername(u.getUsername()));
                }

                codeSnippet.setSharedUsers(sharedUsers);
            }
        }

        return modelMapper.map(codeSnippetRepository.save(codeSnippet), CodeSnippetDTO.class);
    }

    public CodeSnippetDTO findById(Long id) {
        if (codeSnippetRepository.findById(id).isPresent()) {
            return modelMapper.map(codeSnippetRepository.findById(id).get(), CodeSnippetDTO.class);
        } else {
            return null;
        }
    }

    public CodeSnippetDTO updateCodeSnippet(Long id, CodeSnippet codeSnippet) {
        if (codeSnippetRepository.findById(id).isPresent()) {
            CodeSnippet dbSnippet = codeSnippetRepository.findById(id).get();
            dbSnippet.setId(id);

            if (codeSnippet.getTitle() != null) {
                dbSnippet.setTitle(codeSnippet.getTitle());
            }

            try {
                if (!codeSnippet.getCategory().getId().equals(dbSnippet.getCategory().getId())) {
                    dbSnippet.setCategory(codeSnippet.getCategory());
                }
            } catch (NullPointerException e) {
                dbSnippet.setCategory(codeSnippet.getCategory());
            }

            if (codeSnippet.getDescription() != null) {
                dbSnippet.setDescription(codeSnippet.getDescription());
            }

            if (codeSnippet.getCode() != null) {
                dbSnippet.setCode(codeSnippet.getCode());
            }

            dbSnippet.setPublic(codeSnippet.isPublic());

            if (codeSnippet.getTags() != null) {
                dbSnippet.setTags(codeSnippet.getTags());
            }

            if (codeSnippet.getSharedUsers() != null) {
                if (codeSnippet.getSharedUsers().size() > 0) {
                    ArrayList<User> sharedUsers = new ArrayList<>();

                    for (User u : codeSnippet.getSharedUsers()) {
                        sharedUsers.add(userRepository.findUserByUsername(u.getUsername()));
                    }

                    dbSnippet.setSharedUsers(sharedUsers);
                }
            }

            return modelMapper.map(codeSnippetRepository.save(dbSnippet), CodeSnippetDTO.class);
        } else {
            return null;
        }
    }

    public void deleteCodeSnippetById(Long id) {
        codeSnippetRepository.deleteById(id);
    }

    public List<CodeSnippetDTO> findUncategorizedSnippetsOfUser(Long userId) {
        return codeSnippetRepository
                .findUncategorizedSnippetsOfUser(userId)
                .stream()
                .map(this::convertToCodeSnippetDTO)
                .collect(Collectors.toList());
    }

    private CodeSnippetDTO convertToCodeSnippetDTO(CodeSnippet codeSnippet) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper
                .map(codeSnippet, CodeSnippetDTO.class);
    }
}
