package com.ghlabs.snippez.service;

import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
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
        try {
            findUserByUsername(user.getUsername());
        } catch (NullPointerException | IllegalArgumentException e) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return mRepository.save(user);
        }

        return null;
    }

    public UserDTO findUserById(Long id) {
        if (mRepository.findById(id).isPresent()) {
            return modelMapper.map(mRepository.findById(id).get(), UserDTO.class);
        } else {
            return null;
        }
    }

    public UserDTO findUserByUsername(String username) throws NonUniqueResultException {
        return convertToUserDTO(this.mRepository.findUserByUsername(username));
    }

    public UserDTO updateUser(Long id, User user) {
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
                dbUser.setEnabled(user.isEnabled());
            }

            return modelMapper.map(mRepository.save(dbUser), UserDTO.class);
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
        return modelMapper
                .map(user, UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User u = mRepository.findUserByUsername(s);

        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), Arrays.asList(new SimpleGrantedAuthority(u.getRole())));
    }

    public boolean checkUserIsEnabled(String username) {
        return findUserByUsername(username).isEnabled();
    }
}
