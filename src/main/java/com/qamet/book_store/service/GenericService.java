package com.qamet.book_store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GenericService<DTO, ENTITY> {
    void save(DTO dto);

    Page<DTO> findAll(Pageable pageable);

    DTO findById(Long id);

    Optional<DTO> update(Long id, DTO dto);

    void delete(Long id);
}
