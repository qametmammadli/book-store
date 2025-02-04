package com.qamet.book_store.rest.controller;

import com.qamet.book_store.rest.dto.AuthenticationDTO;
import com.qamet.book_store.rest.dto.JWTResponseDTO;
import com.qamet.book_store.security.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping
    public ResponseEntity<JWTResponseDTO> authenticate(@Valid @RequestBody AuthenticationDTO dto) {
        // authenticate
        authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        String token = tokenProvider.createToken(dto.getUsername());
        JWTResponseDTO jwtResponseDTO = new JWTResponseDTO();
        jwtResponseDTO.setAccessToken(token);
        return ResponseEntity.ok(jwtResponseDTO);
    }
}
