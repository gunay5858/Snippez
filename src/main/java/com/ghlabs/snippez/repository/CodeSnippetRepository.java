package com.ghlabs.snippez.repository;

import com.ghlabs.snippez.dto.CodeSnippetDTO;
import com.ghlabs.snippez.entity.CodeSnippet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {
    @Query("SELECT c FROM CodeSnippet c WHERE c.category.id = :categoryId")
    public List<CodeSnippet> findCodeSnippetsOfCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT cs FROM CodeSnippet cs WHERE cs.category is null AND cs.creator.id = :userId")
    public List<CodeSnippet> findUncategorizedSnippetsOfUser(@Param("userId") Long userId);
}
