package com.qamet.book_store.rest.controller;

import com.qamet.book_store.rest.dto.BookDTO;
import com.qamet.book_store.rest.dto.BookSpecDTO;
import com.qamet.book_store.service.BookService;
import com.qamet.book_store.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/books")
public class BookController extends GenericController<BookDTO> {

    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        super(bookService);
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/by_publisher/{publisherId}")
    public ResponseEntity<?> findByPublisher(@PathVariable Integer publisherId) {
        return ResponseEntity.ok(bookService.findAllByPublisher(publisherId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody BookDTO dto) {
        bookService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/published_by_me")
    public ResponseEntity<?> findByMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer publisherId = userService.findByUsername(authentication.getName()).getId();
        return ResponseEntity.ok(bookService.findAllByPublisher(publisherId));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> findByFilter(
            @RequestParam(defaultValue = "", value = "book_name") String bookName,
            @RequestParam(defaultValue = "", value = "book_description") String bookDescription,
            @RequestParam(defaultValue = "", value = "author_name") String authorName,
            @RequestParam(defaultValue = "", value = "publisher_name") String publisherName,
            @RequestParam(defaultValue = "0.0", value = "price_from") BigDecimal fromPrice,
            @RequestParam(required = false, value = "price_to") BigDecimal toPrice,
            Pageable pageable
    ) {
        BookSpecDTO bookSpecDTO = BookSpecDTO.builder()
                .bookName(bookName.trim())
                .bookDescription(bookDescription.trim())
                .authorName(authorName.trim())
                .publisherName(publisherName.trim())
                .priceFrom(fromPrice)
                .priceTo(toPrice == null ? BigDecimal.valueOf(Long.MAX_VALUE) : toPrice)
                .build();


        return ResponseEntity.ok(bookService.findByFilter(bookSpecDTO, pageable));
    }
}
