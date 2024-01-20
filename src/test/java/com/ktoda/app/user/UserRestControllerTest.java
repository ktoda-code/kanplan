package com.ktoda.app.user;

import com.ktoda.app.config.JpaConfig;
import com.ktoda.app.user.dto.UserDTO;
import com.ktoda.app.user.dto.UserRegistrationDTO;
import com.ktoda.app.user.dto.UserUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("tc")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(JpaConfig.class)
@Slf4j
class UserRestControllerTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int randomServerPort;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.0-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void shouldReturnUserList() {
        // Set up
        repository.save(new User("no_replay@kanplan.com", "admin"));

        // When
        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                "http://localhost:" + randomServerPort + "/api/v1/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void shouldFindUserById() {
        // Set up
        User user = repository.save(new User("user1@kanplan.com", "user1"));

        ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                "http://localhost:" + randomServerPort + "/api/v1/users/" + user.getId(),
                UserDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void shouldFindUserByEmail() {
        // Set up
        User user = repository.save(new User("test@example.com", "user1"));

        ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                "http://localhost:" + randomServerPort + "/api/v1/users/email/test@example.com",
                UserDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void shouldCheckUserExistsByEmail() {
        User user = repository.save(new User("test@example.com", "user1"));

        ResponseEntity<Boolean> response = restTemplate.getForEntity(
                "http://localhost:" + randomServerPort + "/api/v1/users/exists/test@example.com",
                Boolean.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    public void shouldCreateUser() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "user2@kanplan.com",
                "user2"
        );

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(
                "http://localhost:" + randomServerPort + "/api/v1/users",
                registrationDTO,
                UserDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void shouldUpdateUser() {
        User user = repository.save(new User("test@example.com", "user1"));

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                user.getId(),
                "newEmail@example.com",
                "newPassword"
        );

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<UserUpdateDTO> requestUpdate = new HttpEntity<>(updateDTO, headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                "http://localhost:" + randomServerPort + "/api/v1/users",
                HttpMethod.PUT,
                requestUpdate,
                UserDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void shouldDeleteUser() {
        User user = repository.save(new User("test@example.com", "user1"));

        restTemplate.delete("http://localhost:" + randomServerPort + "/api/v1/users/" + user.getId());

        ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                "http://localhost:" + randomServerPort + "/api/v1/users/1",
                UserDTO.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}