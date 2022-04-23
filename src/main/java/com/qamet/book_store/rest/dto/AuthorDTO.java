package com.qamet.book_store.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class AuthorDTO extends AbstractDTO {
    private Integer id;

    @NotEmpty
    @Size(min = 1, max = 100)
    private String name;
}
