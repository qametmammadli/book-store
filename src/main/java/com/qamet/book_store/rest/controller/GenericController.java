package com.qamet.book_store.rest.controller;

import com.qamet.book_store.rest.dto.AbstractDTO;
import com.qamet.book_store.rest.errors.ForeignKeyException;
import com.qamet.book_store.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public abstract class GenericController<DTO extends AbstractDTO> {

    private final GenericService<DTO> genericService;

    @GetMapping
    public ResponseEntity<?> findAll()
    {
        return new ResponseEntity<>(genericService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id)
    {
        return new ResponseEntity<>(genericService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody DTO dto)
    {
        genericService.save(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id)
    {
        try {
            genericService.delete(id);
        } catch (DataIntegrityViolationException exp) {
            throw new ForeignKeyException(id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
