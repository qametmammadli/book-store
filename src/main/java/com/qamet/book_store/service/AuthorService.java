package com.qamet.book_store.service;

import com.qamet.book_store.entity.Author;
import com.qamet.book_store.repository.AuthorRepository;
import com.qamet.book_store.rest.dto.AuthorDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AuthorService implements GenericService<AuthorDTO> {
    private final AuthorRepository authorRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public void save(AuthorDTO dto) {
        Author author = new Author();
        author.setName(dto.getName());
        authorRepository.save(author);
    }

    @Override
    public List<AuthorDTO> findAll() {
        return mapper.map(authorRepository.findAll(), new TypeToken<List<AuthorDTO>>() {
        }.getType());
    }

    @Override
    public AuthorDTO findById(Integer id) {
        return null;
    }

    @Override
    public Optional<AuthorDTO> update(Integer id, AuthorDTO dto) {
        return Optional.empty();
    }


    @Override
    public void delete(Integer id) {
        authorRepository.deleteById(id);
    }
}
