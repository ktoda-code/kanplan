package com.ktoda.app.user;

import com.ktoda.app.event.dto.EventCreateDTO;
import com.ktoda.app.event.dto.EventDTO;
import com.ktoda.app.user.dto.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Slf4j
public class UserRestController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll(@RequestParam(required = false, defaultValue = "id")
                                                 String sortBy,
                                                 @RequestParam(required = false, defaultValue = "ASC")
                                                 String sortDir,
                                                 @RequestParam(required = false, defaultValue = "0")
                                                 int pageNumber,
                                                 @RequestParam(required = false, defaultValue = "3")
                                                 int pageElements
    ) {
        if (!Arrays.asList("ASC", "DESC").contains(sortDir.toUpperCase())) {
            return ResponseEntity.badRequest().build();
        }

        if (sortBy != null) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir.toUpperCase()), sortBy);
            if (pageNumber >= 0 && pageElements > 0) {
                Pageable pageable = PageRequest.of(pageNumber, pageElements, sort);
                return ResponseEntity.ok(service.findAll(pageable));
            } else {
                return ResponseEntity.ok(service.findAll(sort));
            }
        } else {
            return ResponseEntity.ok(service.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable("id")
                                            long id) {
        return new ResponseEntity<>(
                service.findById(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findById(@PathVariable("email")
                                            String email) {
        return new ResponseEntity<>(
                service.findByEmail(email),
                HttpStatus.OK
        );
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable("email")
                                                 String email) {
        return new ResponseEntity<>(
                service.existsByEmail(email),
                HttpStatus.OK
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(service.countAll());
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid
                                          @RequestBody UserRegistrationDTO registrationDTO) {
        return new ResponseEntity<>(
                service.create(registrationDTO),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/add/event")
    public ResponseEntity<UserDTO> addEvent(@PathVariable("id") long id,
                                            @Valid @RequestBody EventCreateDTO eventCreateDTO) {
        return new ResponseEntity<>(
                service.addEvent(id, eventCreateDTO),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{userId}/remove/event/{eventId}")
    public ResponseEntity<UserDTO> removeEvent(@PathVariable("userId") long userId,
                                               @PathVariable("eventId") long eventId) {
        return new ResponseEntity<>(
                service.removeEvent(userId, eventId),
                HttpStatus.OK
        );
    }

    @PutMapping
    public ResponseEntity<UserDTO> update(@Valid
                                          @RequestBody UserUpdateDTO updateDTO) {
        return new ResponseEntity<>(
                service.update(updateDTO),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> delete(@PathVariable("id")
                                          long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
