package com.ghlabs.snippez.service;

import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository mRepository;
    int bcryptStrength = 10;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(bcryptStrength, new SecureRandom());

    private final ModelMapper modelMapper = new ModelMapper();

    public UserService(@Autowired UserRepository mRepository) {
        this.mRepository = mRepository;
    }

    public List<UserDTO> findAllUsers() {
        return ((List<User>) mRepository
                .findAll())
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    public User addUser(User user) {
        if (findUserByUsername(user.getUsername()) == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return mRepository.save(user);
        }

        return null;
    }

    public UserDTO findUserById(Long id) {
        if (mRepository.findById(id).isPresent()) {
            // return mRepository.findById(id).get();
            return modelMapper.map(mRepository.findById(id).get(), UserDTO.class);
        } else {
            return null;
        }
    }

    public User findUserByUsername(String username) throws NonUniqueResultException {
        return this.mRepository.findUserByUsername(username);
    }

    public User updateUser(Long id, User user) {
        if (mRepository.findById(id).isPresent()) {
            User dbUser = mRepository.findById(id).get();
            dbUser.setId(id);

            // encrypt password if new one is set
            if (user.getPassword() != null) {
                dbUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }

            if (user.getAvatar() != null) {
                dbUser.setAvatar(user.getAvatar());
            }

            if (user.getEmail() != null) {
                dbUser.setEmail(user.getEmail());
            }

            return mRepository.save(dbUser);
        } else {
            return null;
        }
    }

    public void deleteUserById(Long id) {
        mRepository.deleteById(id);
    }

    private UserDTO convertToUserDTO(User user) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        UserDTO userDTO = modelMapper
                .map(user, UserDTO.class);
        return userDTO;
    }
}
