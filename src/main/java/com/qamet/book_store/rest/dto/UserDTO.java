package com.qamet.book_store.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qamet.book_store.entity.User;
import com.qamet.book_store.validation.UniqueField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO extends AbstractDTO {

    private Integer id;

    @NotEmpty
    @Size(min = 1, max = 50)
    private String firstName;

    @NotEmpty
    @Size(min = 1, max = 50)
    private String lastName;

    @NotEmpty
    @Size(min = 1, max = 50)
    @UniqueField(entityClass = User.class, fieldName = "username")
    private String username;

    @NotEmpty
    @Size(min = 1, max = 50)
    @Email
    @UniqueField(entityClass = User.class, fieldName = "email")
    private String email;

    @NotEmpty
    @Size(min = 6, max = 100)
    private String password;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

}
