package com.ktoda.app.user;

import com.ktoda.app.config.JpaConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Slf4j
class UserRepositoryIntegrationTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    UserRepository repository;

    @Test
    void save_testCreateDateAuditing() {
        // Given
        User given = new User("some@email.com", "test123");

        // When
        User found = repository.save(given);

        // Assert
        assertThat(found.getId()).isNotNegative();
        assertThat(found.getEmail()).isEqualTo("some@email.com");
        assertThat(found.getCreatedDate()).isNotNull();
        assertThat(found.getCreatedDate()).isInstanceOf(Instant.class);
    }

    @Test
    void findUserByEmail_whenUserExists() {
        // Given
        String email = "test@example.com";
        User given = new User(email, "test123");
        entityManager.persist(given);
        entityManager.flush();

        // When
        Optional<User> found = repository.findUserByEmail(email);

        // Assert
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getEmail()).isEqualTo(given.getEmail());
    }

    @Test
    void findUserByEmail_whenUserDoesNotExists() {
        // Given
        String email = "test@example.com";

        // When
        Optional<User> found = repository.findUserByEmail(email);

        // Assert
        Assertions.assertFalse(found.isPresent());
    }

    @Test
    void existsUserByEmail_whenUserExists() {
        // Given
        String email = "test@example.com";
        User user = new User(email, "test123");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean found = repository.existsUserByEmail(email);

        // Assert
        Assertions.assertTrue(found);
    }

    @Test
    void existsUserByEmail_whenUserDoesNotExists() {
        // Given
        String email = "test@example.com";

        // When
        boolean found = repository.existsUserByEmail(email);

        // Assert
        Assertions.assertFalse(found);
    }
}