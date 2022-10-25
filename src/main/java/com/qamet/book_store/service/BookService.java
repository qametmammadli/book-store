package com.qamet.book_store.service;

import com.qamet.book_store.config.producer.KafkaProducer;
import com.qamet.book_store.entity.Author;
import com.qamet.book_store.entity.Book;
import com.qamet.book_store.repository.BookRepository;
import com.qamet.book_store.rest.dto.BookDTO;
import com.qamet.book_store.rest.dto.BookSpecDTO;
import com.qamet.book_store.rest.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService implements GenericService<BookDTO> {

    private final BookRepository bookRepository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final KafkaProducer kafkaProducer;

    @Value("${topics.book.events}")
    private String bookEventsTopic;

    @Override
    public void save(BookDTO dto) {
        Book book = new Book();
        mapToEntity(dto, book);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        book.setPublisherId(userService.findByUsername(authentication.getName()).getId());
        bookRepository.save(book);
        dto.setId(book.getId());
        kafkaProducer.send(bookEventsTopic, dto);
    }

    @Override
    public List<BookDTO> findAll() {
        return mapper.map(bookRepository.findAll(), new TypeToken<List<BookDTO>>() {
        }.getType());
    }

    public Page<BookDTO> findByFilter(BookSpecDTO bookSpecDTO,
                                      Pageable pageable) {

        Page<Book> books = bookRepository.findAllBySpec(bookSpecDTO, pageable);
        return books.map(book -> mapper.map(book, BookDTO.class));
    }

    @Override
    public BookDTO findById(Integer id) {
        return mapper.map(bookRepository.findById(id).orElseThrow(), BookDTO.class);
    }

    public List<BookDTO> findAllByPublisher(Integer publisherId) {
        return mapper.map(bookRepository.findByPublisherId(publisherId), new TypeToken<List<BookDTO>>() {
        }.getType());
    }

    public void update(Integer id, BookDTO dto) {
        Book book = bookRepository.findById(id).orElseThrow();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO username = userService.findByUsername(authentication.getName());
        if (!book.getPublisherId().equals(username.getId())) throw new AccessDeniedException("Publisher can't edit other publishers' books");

        mapToEntity(dto, book);
        bookRepository.save(book);
    }

    private void mapToEntity(BookDTO dto, Book book) {
        book.setName(dto.getName());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        Set<Author> authors = new HashSet<>();
        dto.getAuthorIds().forEach(authorId -> {
            Author author = new Author();
            author.setId(authorId);
            authors.add(author);
        });
        book.setAuthors(authors);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }
}
