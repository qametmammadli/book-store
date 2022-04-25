package com.qamet.book_store;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@AutoConfigureMockMvc
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = BookStoreApplication.class)
@TestPropertySource(
        locations = "classpath:application-test.properties")
@WithMockUser(username = "admin", password = "admin", authorities = {"ROLE_ADMIN", "ROLE_USER", "ROLE_PUBLISHER"})
@Transactional
public @interface IntegrationTest
{
}
