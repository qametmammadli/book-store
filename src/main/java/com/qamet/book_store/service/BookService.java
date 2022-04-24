package com.qamet.book_store.service;

import com.qamet.book_store.entity.Author;
import com.qamet.book_store.entity.Book;
import com.qamet.book_store.repository.BookRepository;
import com.qamet.book_store.rest.dto.BookDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookService implements GenericService<BookDTO> {

    private final BookRepository bookRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public void save(BookDTO dto) {
        Book book = new Book();
        book.setName(dto.getName());
        book.setDescription(dto.getDescription());
        book.setPublisherId(dto.getPublisherId());
        book.setPrice(dto.getPrice());
        Set<Author> authors = new HashSet<>();
        dto.getAuthorIds().forEach(authorId -> {
            Author author = new Author();
            author.setId(authorId);
            authors.add(author);
        });
        book.setAuthors(authors);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getDetails();
        book.setPublisherId(userId);
        bookRepository.save(book);
    }

    @Override
    public List<BookDTO> findAll() {
        return mapper.map(bookRepository.findAll(), new TypeToken<List<BookDTO>>() {}.getType());
    }

    @Override
    public BookDTO findById(Integer id) {
        return null;
    }

    @Override
    public Optional<BookDTO> update(Integer id, BookDTO dto) {
        return Optional.empty();
    }

    @Override
    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }
}
