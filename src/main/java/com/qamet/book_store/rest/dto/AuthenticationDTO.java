package com.qamet.book_store.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class AuthenticationDTO {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
