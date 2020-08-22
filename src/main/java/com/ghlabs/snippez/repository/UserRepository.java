package com.ghlabs.snippez.repository;

import com.ghlabs.snippez.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT distinct u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    public User findUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u")
    public ArrayList<Object> findAllUsers();
}
