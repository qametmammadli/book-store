package com.qamet.book_store.rest.dto;

import com.qamet.book_store.entity.Author;
import com.qamet.book_store.validation.UniqueField;
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
    @UniqueField(entityClass = Author.class, fieldName = "name")
    private String name;
}
