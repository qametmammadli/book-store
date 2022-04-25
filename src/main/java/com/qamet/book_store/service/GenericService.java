package com.qamet.book_store.service;

import com.qamet.book_store.rest.dto.AbstractDTO;

import java.util.List;

public interface GenericService<DTO extends AbstractDTO> {
    void save(DTO dto);

    List<DTO> findAll();

    DTO findById(Integer id);

    void delete(Integer id);
}
