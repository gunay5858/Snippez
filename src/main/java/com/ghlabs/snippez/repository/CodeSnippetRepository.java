package com.ghlabs.snippez.repository;

import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.entity.CodeSnippet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {
    @Query("SELECT c FROM CodeSnippet c WHERE c.category.id = :categoryId")
    public ArrayList<CodeSnippet> findCodeSnippetsOfCategory(@Param("categoryId") Long categoryId);
}
