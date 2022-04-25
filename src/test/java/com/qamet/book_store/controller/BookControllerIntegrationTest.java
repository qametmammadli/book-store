package com.qamet.book_store.controller;

import com.qamet.book_store.IntegrationTest;
import com.qamet.book_store.entity.Author;
import com.qamet.book_store.entity.Book;
import com.qamet.book_store.entity.User;
import com.qamet.book_store.entity.enumeration.RoleName;
import com.qamet.book_store.repository.AuthorRepository;
import com.qamet.book_store.repository.BookRepository;
import com.qamet.book_store.repository.RoleRepository;
import com.qamet.book_store.repository.UserRepository;
import com.qamet.book_store.rest.dto.BookDTO;
import com.qamet.book_store.rest.dto.UserDTO;
import com.qamet.book_store.util.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
public class BookControllerIntegrationTest {
    static final String API_URL = "/api/books";

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    User user;

    BookDTO bookDTO;

    Book book;

    @Autowired
    ModelMapper mapper;

    @BeforeEach
    public void setUp() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@bookstore.com");
        userDTO.setFirstName("First name");
        userDTO.setLastName("Last name");
        userDTO.setUsername("username");
        userDTO.setPassword("1test1");

        Author author = new Author();
        author.setName("Author name");
        authorRepository.save(author);

        bookDTO = new BookDTO();
        bookDTO.setName("test book");
        bookDTO.setDescription("test book description");
        bookDTO.setPrice(BigDecimal.ONE);
        bookDTO.setAuthorIds(new HashSet<>(Collections.singleton(author.getId())));


        user = new User();
        mapper.map(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(new HashSet<>(Collections.singleton(roleRepository.findByName(RoleName.PUBLISHER))));
        userRepository.save(user);

        book = new Book();
        mapper.map(bookDTO, book);
        book.setAuthors(new HashSet<>(Collections.singleton(author)));
        book.setPublisherId(user.getId());
    }

    @Test
    void shouldCreateNewBook() throws Exception {
        // before save
        Assertions.assertEquals(0, bookRepository.count());

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(bookDTO)))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
    }

    @Test
    void shouldReturnValidationsWhenTryToCreateBook_WithNullFields() throws Exception {
        BookDTO emptyBookDTO = new BookDTO();

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(emptyBookDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.length()", equalTo(3)));
    }


    @Test
    void shouldListBooks() throws Exception {
        bookRepository.save(book);

        mockMvc.perform(get(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetOneBook() throws Exception {
        bookRepository.save(book);

        mockMvc.perform(get(API_URL + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", equalTo(book.getName())))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetBooksByPublisherId() throws Exception {
        bookRepository.save(book);

        mockMvc.perform(get(API_URL + "/by_publisher/" + book.getPublisherId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    @WithMockUser(username = "username", password = "1test1", authorities = "ROLE_PUBLISHER")
    void shouldGetBooks_PublishedByMe() throws Exception {
        bookRepository.save(book);

        mockMvc.perform(get(API_URL + "/published_by_me")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    void shouldReturn404Error_whenTryToGetOneBook() throws Exception {
        mockMvc.perform(get(API_URL + "/" + 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteBook() throws Exception {
        bookRepository.save(book);
        Assertions.assertEquals(1, bookRepository.count());

        mockMvc.perform(delete(API_URL + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, bookRepository.count());
    }

    @Test
    void shouldReturnBooksByFilter() throws Exception {
        bookRepository.save(book);

        String filterQueryParams = "size=2" +
                "&" +
                "page=0" +
                "&" +
                "author_name=AUthor" +
                "&" +
                "publisher_name=FiRSt name Last" +
                "&" +
                "price_from=1" +
                "&" +
                "book_name=tEst" +
                "&" +
                "price_to=2" +
                "&" +
                "book_description=DESc";

        mockMvc.perform(get(API_URL + "/filter?" + filterQueryParams)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", containsString("test")))
                .andExpect(jsonPath("$.content[0].price", equalTo(1)))
                .andExpect(jsonPath("$.content[0].description", containsString("description")))
                .andExpect(jsonPath("$.first", equalTo(Boolean.TRUE)))
                .andExpect(jsonPath("$.last", equalTo(Boolean.TRUE)))
                .andExpect(jsonPath("$.number_of_elements", equalTo(1)))
                .andExpect(jsonPath("$.number", equalTo(0)))
                .andExpect(jsonPath("$.size", equalTo(2)));
    }

    @Test
    void shouldReturnEmptyArray_whenTryToGetBooksByFilter_withInvalidParams() throws Exception {
        bookRepository.save(book);

        String filterQueryParams = "size=2" +
                "&" +
                "page=0" +
                "&" +
                "author_name=atre" +
                "&" +
                "publisher_name=ff name" +
                "&" +
                "price_from=1" +
                "&" +
                "book_name=invalid test" +
                "&" +
                "price_to=2" +
                "&" +
                "book_description=invalid DESc";

        mockMvc.perform(get(API_URL + "/filter?" + filterQueryParams)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.first", equalTo(Boolean.TRUE)))
                .andExpect(jsonPath("$.last", equalTo(Boolean.TRUE)))
                .andExpect(jsonPath("$.empty", equalTo(Boolean.TRUE)))
                .andExpect(jsonPath("$.number_of_elements", equalTo(0)))
                .andExpect(jsonPath("$.number", equalTo(0)))
                .andExpect(jsonPath("$.size", equalTo(2)));
    }

    @Test
    void shouldReturn404_whenTryToDeleteBook_notExist() throws Exception {
        mockMvc.perform(delete(API_URL + "/" + 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "username", password = "1test1", authorities = "ROLE_PUBLISHER")
    void shouldUpdateBook_CreatedBySamePublisher() throws Exception {
        bookRepository.save(book);

        Author author2 = new Author();
        author2.setName("author2");
        authorRepository.save(author2);

        bookDTO.setName("updated");
        bookDTO.setDescription("updated");
        bookDTO.setPrice(BigDecimal.TEN);
        bookDTO.setAuthorIds(new HashSet<>(Collections.singleton(author2.getId())));

        mockMvc.perform(put(API_URL + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(bookDTO))
        )
                .andExpect(status().isOk());

        Book book = bookRepository.findById(this.book.getId()).orElseThrow();
        Assertions.assertEquals(book.getName(), "updated");
        Assertions.assertEquals(book.getDescription(), "updated");
        Assertions.assertEquals(book.getPrice(), BigDecimal.TEN);
        Assertions.assertTrue(book.getAuthors().stream().allMatch(author -> author.getId().equals(author2.getId())));
        Assertions.assertEquals(book.getPublisherId(), user.getId());
    }

    @Test
    void shouldReturn403_whenTryToUpdateBook_CreatedByOtherPublisher() throws Exception {
        bookRepository.save(book);

        mockMvc.perform(put(API_URL + "/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(bookDTO))
        )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", equalTo("Forbidden")))
                .andExpect(jsonPath("$.detail", equalTo("Publisher can't edit other publishers' books")));
    }

}
