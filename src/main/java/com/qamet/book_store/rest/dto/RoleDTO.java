package com.qamet.book_store.rest.dto;

import com.qamet.book_store.entity.Role;
import com.qamet.book_store.entity.enumeration.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

    private RoleName name;

    public RoleDTO(Role role) {
        this.name = role.getName();
    }

}
