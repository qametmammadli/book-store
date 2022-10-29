package com.qamet.book_store.runner;

import com.qamet.book_store.entity.Role;
import com.qamet.book_store.entity.User;
import com.qamet.book_store.entity.enumeration.RoleName;
import com.qamet.book_store.repository.RoleRepository;
import com.qamet.book_store.repository.UserRepository;
import com.qamet.book_store.rest.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, BookDTO> kafkaTemplate;

    @Value("${topics.book.events}")
    private String bookEventsTopic;

    @Override
    public void run(ApplicationArguments args) {

        BookDTO testBook = BookDTO.builder()
                .id(1)
                .name("test book")
                .build();

        kafkaTemplate.send(bookEventsTopic, testBook);

        if (roleRepository.count() < 1) {
            Role userRole = new Role(null, RoleName.USER);
            Role adminRole = new Role(null, RoleName.ADMIN);
            Role publisherRole = new Role(null, RoleName.PUBLISHER);
            roleRepository.saveAll(Arrays.asList(adminRole, userRole, publisherRole));
        }

        if (userRepository.count() < 1) {
            User admin = new User();
            admin.setFirstName("Admin name");
            admin.setLastName("Lastname");
            admin.setUsername("admin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin1"));
            admin.setRoles(new HashSet<>(roleRepository.findAll()));
            userRepository.save(admin);
        }

    }
}
