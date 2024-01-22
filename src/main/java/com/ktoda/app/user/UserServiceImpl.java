package com.ktoda.app.user;

import com.ktoda.app.event.Event;
import com.ktoda.app.event.EventNotification;
import com.ktoda.app.event.EventProperty;
import com.ktoda.app.event.dto.EventCreateDTO;
import com.ktoda.app.event.dto.EventDTO;
import com.ktoda.app.password.PasswordService;
import com.ktoda.app.user.dto.UserDTO;
import com.ktoda.app.user.dto.UserDTOMapper;
import com.ktoda.app.user.dto.UserRegistrationDTO;
import com.ktoda.app.user.dto.UserUpdateDTO;
import com.ktoda.app.user.exception.UserAlreadyExistsException;
import com.ktoda.app.user.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserDTOMapper dtoMapper;
    private final PasswordService passwordService;

    @Override
    public List<UserDTO> findAll(Sort sort) {
        return repository.findAll(sort)
                .stream()
                .map(dtoMapper)
                .toList();
    }

    @Override
    public List<UserDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .stream()
                .map(dtoMapper)
                .toList();
    }

    @Override
    public Page<UserDTO> findAllPage(Pageable pageable) {
        Page<User> userPage = repository.findAll(pageable);

        List<UserDTO> dtoList = userPage.getContent()
                .stream()
                .map(dtoMapper) // assuming dtoMapper is a Function<User, UserDTO>
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    @Override
    public List<UserDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(dtoMapper)
                .toList();
    }

    @Override
    public UserDTO findById(Long id) {
        return repository.findById(id)
                .map(dtoMapper)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDTO findByEmail(String email) {
        return repository.findUserByEmail(email)
                .map(dtoMapper)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsUserByEmail(email);
    }

    @Override
    public UserDTO save(User entity) {
        log.info("Saving user with email: {}", entity.getEmail());
        return dtoMapper.apply(repository.save(entity));
    }

    @Override
    public UserDTO create(UserRegistrationDTO entity) {
        log.info("Creating new user with email: {}", entity.email());
        if (existsByEmail(entity.email())) {
            log.warn("Attempted to create a user that already exists with email: {}", entity.email());
            throw new UserAlreadyExistsException("User already exists with this information");
        }

        String rawPassword = entity.password();
        String hashedPassword = passwordService.hash(rawPassword);
        User user = new User(entity.email(), hashedPassword);

        return save(user);
    }

    @Override
    public UserDTO update(UserUpdateDTO entity) {
        log.info("Updating user with id: {}", entity.id());
        Optional<User> user = repository.findById(entity.id());
        User managedUser;

        if (user.isPresent()) {
            managedUser = user.get();
        } else {
            log.error("Attempted to update a non-existing id: {}", entity.id());
            throw new UserNotFoundException("User not found with this information");
        }

        managedUser.setEmail(entity.email());
        managedUser.setPassword(entity.password());

        return save(managedUser);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!repository.existsById(id)) {
            log.error("Attempted to delete a non-existing user with id: {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public long countAll() {
        return repository.count();
    }

    @Override
    public UserDTO addEvent(long id, EventCreateDTO eventCreateDTO) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        EventProperty property = new EventProperty(
                eventCreateDTO.property().date(),
                eventCreateDTO.property().startTime(),
                eventCreateDTO.property().endTime(),
                eventCreateDTO.property().allDay()
        );

        EventNotification notification = new EventNotification(
                eventCreateDTO.notification().interval(),
                eventCreateDTO.notification().type()
        );

        Event event = new Event(eventCreateDTO.title(),
                eventCreateDTO.description(),
                eventCreateDTO.color(),
                property,
                notification);

        user.addEvent(event);
        return save(user);
    }

    @Override
    public UserDTO removeEvent(long userId, long eventId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Event eventToRemove = user.getEvents().stream()
                .filter(e -> e.getId() == eventId)
                .findFirst()
                .orElse(null);

        // If the event is found, remove it
        if (eventToRemove != null) {
            user.removeEvent(eventToRemove);
        }

        return save(user);
    }
}
