package com.qamet.book_store.service;

import com.qamet.book_store.rest.dto.AbstractDTO;

import java.util.List;
import java.util.Optional;

public interface GenericService<DTO extends AbstractDTO> {
    void save(DTO dto);

    List<DTO> findAll();

    DTO findById(Integer id);

    Optional<DTO> update(Integer id, DTO dto);

    void delete(Integer id);
}
