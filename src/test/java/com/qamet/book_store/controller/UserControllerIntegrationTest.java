package com.qamet.book_store.controller;

import com.qamet.book_store.IntegrationTest;
import com.qamet.book_store.entity.User;
import com.qamet.book_store.entity.enumeration.RoleName;
import com.qamet.book_store.repository.RoleRepository;
import com.qamet.book_store.repository.UserRepository;
import com.qamet.book_store.rest.dto.UserDTO;
import com.qamet.book_store.util.JsonUtil;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashSet;

@IntegrationTest
class UserControllerIntegrationTest {
    static final String API_URL = "/users";

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    UserDTO userDTO;
    User user;

    @BeforeEach
    public void setUp() {
        userDTO = new UserDTO();
        userDTO.setEmail("test@bookstore.com");
        userDTO.setFirstName("First name");
        userDTO.setLastName("Last name");
        userDTO.setUsername("username");
        userDTO.setPassword("1test1");

        user = new User();
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(new HashSet<>(Collections.singleton(roleRepository.findByName(RoleName.USER))));
    }

    @Test
    void shouldCreateNewUser() throws Exception {

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(userDTO)))
                .andExpect(status().isCreated());

        User createdUser = userRepository.findByUsername(userDTO.getUsername()).orElseThrow();
        Assertions.assertEquals(createdUser.getUsername(), "username");
        Assertions.assertEquals(createdUser.getEmail(), "test@bookstore.com");
        Assertions.assertTrue(passwordEncoder.matches("1test1", createdUser.getPassword()));
    }

    @Test
    void shouldReturnValidationsWhenTryToCreateUser_WithNullFields() throws Exception {
        UserDTO emptyUserDTO = new UserDTO();

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(emptyUserDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.length()", equalTo(5)));
    }

    @Test
    void shouldReturnUniqueUsernameValidationWhenTryToCreateUser_WithExistedUsername() throws Exception {
        //  admin is created by ApplicationRunner (see runner.DataSeeder)
        userDTO.setUsername("admin");

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(userDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.length()", equalTo(1)))
                .andExpect(jsonPath("$.invalid_params.username[0]", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.username[0]", equalTo("This value is already exist")));
    }

    @Test
    void shouldReturnUniqueEmailValidationWhenTryToCreateUser_WithExistedEmail() throws Exception {
        //  admin@test.com is created by ApplicationRunner (see runner.DataSeeder)
        userDTO.setEmail("admin@test.com");

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(userDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.length()", equalTo(1)))
                .andExpect(jsonPath("$.invalid_params.email[0]", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.email[0]", equalTo("This value is already exist")));
    }

    @Test
    void shouldReturnSizeValidationsWhenTryToCreateUser_WithInvalidSizeFields() throws Exception {
        userDTO.setUsername("");
        userDTO.setEmail("");
        userDTO.setFirstName("");
        userDTO.setLastName("");
        userDTO.setPassword("");

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(userDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.length()", equalTo(5)))
                .andExpect(jsonPath("$.invalid_params.username[0]", notNullValue()))
                .andExpect(jsonPath("$.invalid_params.email[0]", notNullValue()));
    }

    @Test
    void shouldListUsers() throws Exception {

        mockMvc.perform(get(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("admin")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetOneUser() throws Exception {
        User user = userRepository.findByUsername("admin").orElseThrow();

        mockMvc.perform(get(API_URL + "/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldAddPublisherRoleToUser() throws Exception {
        userRepository.save(user);

        mockMvc.perform(put(API_URL + "/" + user.getId() + "/add_publisher_role")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        User user = userRepository.findByUsername(this.user.getUsername()).orElseThrow();
        Assertions.assertTrue(user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.PUBLISHER)));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities = "ROLE_USER")
    void shouldReturn403AccessDenied_WhenTryToAddPublisherRoleToUser_WithNotAdminRole() throws Exception {
        userRepository.save(user);

        mockMvc.perform(put(API_URL + "/" + user.getId() + "/add_publisher_role")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden());

        User user = userRepository.findByUsername(this.user.getUsername()).orElseThrow();
        Assertions.assertFalse(user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.PUBLISHER)));
    }

    @Test
    void shouldReturn404Error_whenTryToAddPublisherRoleToUser() throws Exception {
        userRepository.save(user);

        mockMvc.perform(put(API_URL + "/" + 0 + "/add_publisher_role")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());

        User user = userRepository.findByUsername(this.user.getUsername()).orElseThrow();
        Assertions.assertFalse(user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.PUBLISHER)));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        userRepository.save(user);
        // has 2 users in db (admin, user)
        Assertions.assertEquals(2, userRepository.count());

        mockMvc.perform(delete(API_URL + "/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

        Assertions.assertEquals(1, userRepository.count());
    }
}
