package com.ktoda.app.user;

import com.ktoda.app.config.JpaConfig;
import com.ktoda.app.user.dto.UserDTO;
import com.ktoda.app.user.dto.UserRegistrationDTO;
import com.ktoda.app.user.dto.UserUpdateDTO;
import com.ktoda.app.user.exception.UserAlreadyExistsException;
import com.ktoda.app.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("tc")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(JpaConfig.class)
@Slf4j
@DirtiesContext
class UserServiceImplIntegrationTest {
    @Autowired
    UserService service;
    @Autowired
    UserRepository repository;

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
        log.info("Cleaning up database after test!");
        repository.deleteAll();
    }

    @Test
    void findAll() {
        // Given
        service.save(new User("test1@user.com", "user1"));
        service.save(new User("test2@user.com", "user2"));

        // When
        List<UserDTO> userDTOS = service.findAll();

        // Assert
        assertThat(userDTOS).hasSize(2);
        assertThat(userDTOS.get(1).email()).isEqualTo("test2@user.com");
    }

    @Test
    void creat_whenUserNotExists() {
        // Given
        UserRegistrationDTO userDTO = new UserRegistrationDTO(
                "user1@test.com",
                "user1"
        );


        // When
        UserDTO created = service.create(userDTO);

        // Assert
        assertNotNull(created);
        assertThat(created.email()).isEqualTo(userDTO.email());
    }

    @Test
    void creat_whenUserExists() {
        // Set up
        User inDb = new User("user1@test.com", "user1");
        service.save(inDb);

        // Given
        UserRegistrationDTO userDTO = new UserRegistrationDTO(
                "user1@test.com",
                "user1"
        );

        // When & Assert
        assertThrows(UserAlreadyExistsException.class,
                () -> service.create(userDTO));
    }

    @Test
    void update_whenUserExists() {
        // Set up
        User inDb = new User(1L, "user1@test.com", "user1", Instant.now(),null);
        User user = repository.save(inDb);
        assertThat(service.findAll()).hasSize(1);

        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO(
                user.getId(),
                "user2@test.com",
                "user1"
        );

        // When
        UserDTO updated = service.update(updateDTO);

        // Assert
        assertNotNull(updated);
        assertThat(updated.email()).isEqualTo(updateDTO.email());
    }

    @Test
    void update_whenUserNotExists() {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO(
                1L,
                "user2@test.com",
                "user1"
        );

        // When & Assert
        assertThrows(UserNotFoundException.class,
                () -> service.update(updateDTO));
    }

    @Test
    void deleteById_whenUserExists() {
        // Set up
        User inDb = new User(1L, "user1@test.com", "user1", Instant.now(),null);
        User user = repository.save(inDb);

        assertThat(service.findAll()).hasSize(1);

        // Given
        long userId = user.getId();

        // When
        service.deleteById(userId);

        // Assert
        assertThat(service.findAll()).hasSize(0);
    }

    @Test
    void deleteById_whenUserNotExists() {
        // Given
        long userId = 1L;

        // When & Assert
        assertThrows(UserNotFoundException.class, () -> service.deleteById(userId));
    }
}