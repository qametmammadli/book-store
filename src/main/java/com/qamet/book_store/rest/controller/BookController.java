package com.qamet.book_store.rest.controller;

import com.qamet.book_store.rest.dto.BookDTO;
import com.qamet.book_store.service.BookService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController extends GenericController<BookDTO> {
    public BookController(BookService bookService) {
        super(bookService);
    }
}
