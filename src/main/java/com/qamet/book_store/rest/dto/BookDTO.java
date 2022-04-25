package com.qamet.book_store.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO extends AbstractDTO {
    private Integer id;

    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

    private String description;

    @NotEmpty
    private Set<Integer> authorIds;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal price = BigDecimal.ZERO;

    private UserDTO publisher;

    private Set<AuthorDTO> authors;

}
