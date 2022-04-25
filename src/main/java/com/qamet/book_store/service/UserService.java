package com.qamet.book_store.service;

import com.qamet.book_store.entity.Role;
import com.qamet.book_store.entity.User;
import com.qamet.book_store.entity.enumeration.RoleName;
import com.qamet.book_store.repository.RoleRepository;
import com.qamet.book_store.repository.UserRepository;
import com.qamet.book_store.rest.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService implements GenericService<UserDTO> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public void save(UserDTO userDTO) {
        User user = mapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(new HashSet<>(Collections.singleton(roleRepository.findByName(RoleName.USER))));
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Integer id) {
        return userRepository.findById(id).map(UserDTO::new).orElseThrow();
    }

    @Transactional
    public void addPublisherRole(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        user.getRoles().add(roleRepository.findByName(RoleName.PUBLISHER));
        userRepository.save(user);
    }

    public UserDTO findByUsername(String username) {
        return mapper.map(userRepository.findByUsername(username).orElseThrow(), UserDTO.class);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        userRepository.findById(id).ifPresentOrElse(user -> {
            if (user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()).contains(RoleName.ADMIN))
                throw new AccessDeniedException("Admin can't be deleted");

            userRepository.deleteById(id);
        }, () -> {
            throw new NoSuchElementException();
        });
    }
}

