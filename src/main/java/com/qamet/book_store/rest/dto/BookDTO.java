package com.qamet.book_store.rest.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class BookDTO extends AbstractDTO {
    private Integer id;

    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

    private String description;

    @NotNull
    private Integer publisherId;

    @NotEmpty
    private Set<Integer> authorIds;

    private UserDTO publisher;

    private Set<AuthorDTO> authors;

}
