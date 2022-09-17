package com.qamet.book_store.rest.controller;

import com.qamet.book_store.rest.dto.AuthorDTO;
import com.qamet.book_store.service.AuthorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authors")
public class AuthorController extends GenericController<AuthorDTO> {
    public AuthorController(AuthorService authorService) {
        super(authorService);
    }
}
