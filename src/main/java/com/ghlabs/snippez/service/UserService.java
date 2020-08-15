package com.ghlabs.snippez.service;

import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.security.SecureRandom;
import java.util.ArrayList;

@Service
public class UserService {
    private final UserRepository mRepository;
    int bcryptStrength = 10;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(bcryptStrength, new SecureRandom());

    public UserService(@Autowired UserRepository mRepository) {
        this.mRepository = mRepository;
    }

    public ArrayList<Object> findAllUsers() {
        return mRepository.findAllUsers();
    }

    public User addUser(User user) {
        if (findUserByUsername(user.getUsername()) == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return mRepository.save(user);
        }

        return null;
    }

    public User findUserById(Long id) {
        if (mRepository.findById(id).isPresent()) {
            return mRepository.findById(id).get();
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
}
