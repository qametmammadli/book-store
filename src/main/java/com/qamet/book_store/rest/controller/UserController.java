package com.qamet.book_store.rest.controller;

import com.qamet.book_store.rest.dto.UserDTO;
import com.qamet.book_store.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends GenericController<UserDTO> {
    public UserController(UserService userService) {
        super(userService);
    }
}
