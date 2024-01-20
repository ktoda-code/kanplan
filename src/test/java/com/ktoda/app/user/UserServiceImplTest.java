package com.ktoda.app.user;

import com.ktoda.app.user.dto.UserDTO;
import com.ktoda.app.user.dto.UserDTOMapper;
import com.ktoda.app.user.dto.UserRegistrationDTO;
import com.ktoda.app.user.dto.UserUpdateDTO;
import com.ktoda.app.user.exception.UserAlreadyExistsException;
import com.ktoda.app.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    UserRepository repository;
    @Mock
    UserDTOMapper dtoMapper;
    @InjectMocks
    UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_whenUserExist() {
        // Arrange
        long existingId = 1L;
        String email = "test@example.com";
        Instant instantNow = Instant.now();
        User user = new User(existingId, email, "test123", instantNow);
        UserDTO userDTO = new UserDTO(existingId, email, instantNow);
        when(repository.findById(existingId)).thenReturn(Optional.of(user));
        when(dtoMapper.apply(user)).thenReturn(userDTO);

        // Act
        UserDTO found = service.findById(existingId);

        // Assert
        assertThat(found.id()).isEqualTo(existingId);
        assertThat(found.email()).isEqualTo(email);
    }

    @Test
    void findById_whenUserDoesNotExist() {
        // Arrange
        long nonExistingId = 0L;
        when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> service.findById(nonExistingId));
    }


    @Test
    void findByEmail_whenUserExist() {
        // Arrange
        long existingId = 1L;
        String existingEmail = "test@example.com";
        Instant instantNow = Instant.now();
        User user = new User(existingId, existingEmail, "test123", instantNow);
        UserDTO userDTO = new UserDTO(existingId, existingEmail, instantNow);
        when(repository.findUserByEmail(existingEmail)).thenReturn(Optional.of(user));
        when(dtoMapper.apply(user)).thenReturn(userDTO);

        // Act
        UserDTO found = service.findByEmail(existingEmail);

        // Assert
        assertThat(found.email()).isEqualTo(existingEmail);
        assertEquals(found, userDTO);
    }

    @Test
    void findByEmail_whenUserDoesNotExist() {
        // Arrange
        String nonExistingEmail = "test@notExisting.com";
        when(repository.findUserByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> service.findByEmail(nonExistingEmail));
    }

    @Test
    void create_whenUserDoesNotExist() {
        // Arrange
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "test@example.com",
                "test123"
        );
        User user = new User(
                registrationDTO.email(),
                registrationDTO.password()
        );
        Instant creationInstant = Instant.now();
        long id = anyLong();
        User savedUser = new User(
                id,
                user.getEmail(),
                user.getPassword(),
                creationInstant
        );
        UserDTO userDTO = new UserDTO(id, savedUser.getEmail(), creationInstant);
        when(service.existsByEmail(registrationDTO.email())).thenReturn(false);
        when(service.save(user)).thenReturn(userDTO);

        // Act
        UserDTO found = service.create(registrationDTO);

        // Assert
        assertThat(found.email()).isEqualTo(user.getEmail());
        assertThat(found.createdOn()).isEqualTo(creationInstant);
    }

    @Test
    void create_whenUserExist() {
        // Arrange
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "notExisting@example.com",
                "test123"
        );
        when(service.existsByEmail(registrationDTO.email())).thenReturn(true);

        // Act and Assert
        assertThrows(UserAlreadyExistsException.class, () -> service.create(registrationDTO));
    }

    @Test
    void update_whenUserDoesNotExist() {
        // Arrange
        UserUpdateDTO userToUpdate = new UserUpdateDTO(
                1L,
                "nonexistent@example.com",
                "newPassword"
        );
        when(repository.findById(userToUpdate.id())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> service.update(userToUpdate));
    }

    @Test
    void update_whenUserExists() {
        // Arrange
        String emailToUpdate = "existing@example.com";
        UserUpdateDTO userToUpdate = new UserUpdateDTO(1L, emailToUpdate, "newPassword");
        Instant createdInstant = Instant.now();
        User existingUser = new User(
                1L,
                emailToUpdate,
                "oldPassword",
                createdInstant
        );
        User updatedUser = new User(
                1L,
                emailToUpdate,
                userToUpdate.password(),
                createdInstant
        );
        UserDTO userDTO = new UserDTO(1L, updatedUser.getEmail(), createdInstant);
        when(repository.findById(userToUpdate.id())).thenReturn(Optional.of(existingUser));
        when(service.save(updatedUser)).thenReturn(userDTO);

        // Act
        UserDTO found = service.update(userToUpdate);

        // Assert
        assertThat(found.email()).isEqualTo(updatedUser.getEmail());
    }

    @Test
    void deleteById_whenUserExist() {
        // Arrange
        long existingId = 1L;
        when(repository.existsById(existingId)).thenReturn(true);

        // Act
        service.deleteById(existingId);

        // Assert
        verify(repository).deleteById(existingId);
    }

    @Test
    void deleteById_whenUserDoesNotExist() {
        // Arrange
        long nonExistingId = 2L;
        when(repository.existsById(nonExistingId)).thenReturn(false);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> service.deleteById(nonExistingId));
    }


}