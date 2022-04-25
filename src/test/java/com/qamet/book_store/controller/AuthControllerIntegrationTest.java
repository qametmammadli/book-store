package com.qamet.book_store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.qamet.book_store.IntegrationTest;
import com.qamet.book_store.rest.dto.AuthenticationDTO;
import com.qamet.book_store.rest.dto.JWTResponseDTO;
import com.qamet.book_store.security.TokenProvider;
import com.qamet.book_store.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class AuthControllerIntegrationTest {

    private static final String API_URL = "/api/authentication";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TokenProvider tokenProvider;

    @Test
    void shouldSuccessfullyAuthenticate_whenTryToLogin() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setUsername("admin");
        authenticationDTO.setPassword("admin1");

        MvcResult mvcResult = mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(authenticationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andReturn();

        String tokenAsJson = mvcResult.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        JWTResponseDTO jwtResponseDTO = objectMapper.readValue(tokenAsJson, JWTResponseDTO.class);

        assertTrue(tokenProvider.validateToken(jwtResponseDTO.getAccessToken()));
    }

    @Test
    void shouldReturnEmptyValidationErrors_whenTryToLogin_withEmptyFields() throws Exception {
        AuthenticationDTO emptyAuthenticationDTO = new AuthenticationDTO();

         mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.convertObjectToJson(emptyAuthenticationDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.invalid_params.length()", equalTo(2)));

    }

}
