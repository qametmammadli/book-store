package com.qamet.book_store.rest.controller;

import com.qamet.book_store.rest.dto.AbstractDTO;
import com.qamet.book_store.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class GenericController<DTO extends AbstractDTO> {

    private final GenericService<DTO> genericService;

    @GetMapping
    public ResponseEntity<?> findAll()
    {
        return new ResponseEntity<>(genericService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody DTO dto)
    {
        genericService.save(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
