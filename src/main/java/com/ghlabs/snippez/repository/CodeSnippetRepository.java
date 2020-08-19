package com.ghlabs.snippez.repository;

import com.ghlabs.snippez.entity.CodeSnippet;
import org.springframework.data.repository.CrudRepository;

public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {
}
