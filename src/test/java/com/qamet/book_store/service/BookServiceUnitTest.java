package com.qamet.book_store.service;

import com.qamet.book_store.config.producer.KafkaProducer;
import com.qamet.book_store.entity.Book;
import com.qamet.book_store.repository.BookRepository;
import com.qamet.book_store.rest.dto.BookDTO;
import com.qamet.book_store.rest.dto.UserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

class BookServiceUnitTest {

    BookDTO bookDTO;

    Book book;

    BookRepository bookRepository;

    UserService userService;

    ModelMapper mapper;

    BookService bookService;

    KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        bookRepository = mock(BookRepository.class);
        mapper = mock(ModelMapper.class);
        this.bookService = new BookService(bookRepository, userService, mapper, kafkaProducer);

        bookDTO = new BookDTO();
        bookDTO.setName("test book");
        bookDTO.setDescription("test book description");
        bookDTO.setPrice(BigDecimal.ONE);
        bookDTO.setAuthorIds(new HashSet<>(Collections.singleton(1)));

        book = new Book();
        book.setName(bookDTO.getName());
        book.setDescription(bookDTO.getDescription());
        book.setPublisherId(1);
        book.setPrice(BigDecimal.ONE);
    }


    @Test
    void shouldSaveBook() {
        when(userService.findByUsername(anyString())).thenReturn(new UserDTO());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", new ArrayList<>()));

        bookService.save(bookDTO);

        verify(bookRepository).save(any(Book.class));
        verify(userService).findByUsername(anyString());
        verifyNoMoreInteractions(bookRepository, userService);
    }

    @Test
    void shouldGetAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(book);

        List<BookDTO> bookDTOS = new ArrayList<>() {{
            add(bookDTO);
        }};

        when(bookRepository.findAll()).thenReturn(books);

        when(mapper.map(books,
                new TypeToken<List<BookDTO>>() {
                }.getType())
        ).thenReturn(bookDTOS);

        List<BookDTO> all = bookService.findAll();

        assertEquals(1, all.size());

        verify(bookRepository).findAll();

        verify(mapper).map(books,
                new TypeToken<List<BookDTO>>() {
                }.getType()
        );

        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(userService);
    }

    @Test
    void shouldGetOneBook() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));
        when(mapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO fromDB = bookService.findById(anyInt());

        assertEquals(book.getName(), fromDB.getName());
        assertEquals(book.getDescription(), fromDB.getDescription());
        assertEquals(book.getPrice(), fromDB.getPrice());

        verify(bookRepository).findById(anyInt());
        verify(mapper).map(book, BookDTO.class);
        verifyNoMoreInteractions(bookRepository, mapper);
        verifyNoInteractions(userService);
    }


    @Test
    void shouldGetBooksByPublisher() {
        List<Book> books = new ArrayList<>();
        books.add(book);

        List<BookDTO> bookDTOS = new ArrayList<>() {{
            add(bookDTO);
        }};

        when(bookRepository.findByPublisherId(anyInt())).thenReturn(books);

        when(mapper.map(books,
                new TypeToken<List<BookDTO>>() {
                }.getType())
        ).thenReturn(bookDTOS);

        List<BookDTO> all = bookService.findAllByPublisher(anyInt());

        assertEquals(1, all.size());

        verify(bookRepository).findByPublisherId(anyInt());

        verify(mapper).map(books,
                new TypeToken<List<BookDTO>>() {
                }.getType()
        );

        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(userService);
    }

    @Test
    void shouldUpdateBook() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(book.getPublisherId());

        when(userService.findByUsername(anyString())).thenReturn(userDTO);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", new ArrayList<>()));

        bookDTO.setName("update");
        bookDTO.setDescription("update");
        bookDTO.setPrice(BigDecimal.TEN);

        bookService.update(anyInt(), bookDTO);

        assertEquals("update", book.getName());
        assertEquals("update", book.getDescription());
        assertEquals(BigDecimal.TEN, book.getPrice());

        verify(bookRepository).findById(anyInt());
        verify(bookRepository).save(any(Book.class));
        verify(userService).findByUsername(anyString());
        verifyNoMoreInteractions(bookRepository, userService);
    }

    @Test
    void shouldThrowAccessDeniedException_whenTryToUpdateOtherPublisherBook() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));

        UserDTO userDTO = new UserDTO();
        userDTO.setId(book.getPublisherId() + 234);

        when(userService.findByUsername(anyString())).thenReturn(userDTO);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", new ArrayList<>()));

        assertThrows(AccessDeniedException.class, () ->
                bookService.update(anyInt(), bookDTO));

        verify(bookRepository).findById(anyInt());
        verify(userService).findByUsername(anyString());
        verifyNoMoreInteractions(bookRepository, userService);
    }

    @Test
    void shouldDeleteBook() {
        doNothing().when(bookRepository).deleteById(anyInt());

        bookService.delete(anyInt());

        verify(bookRepository).deleteById(anyInt());
        verifyNoMoreInteractions(bookRepository);
    }
}
