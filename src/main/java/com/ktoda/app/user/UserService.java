package com.ktoda.app.user;

import com.ktoda.app.user.dto.UserDTO;
import com.ktoda.app.user.dto.UserRegistrationDTO;
import com.ktoda.app.user.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> findAll(Sort sort);

    List<UserDTO> findAll(Pageable pageable);

    Page<UserDTO> findAllPage(Pageable pageable);

    List<UserDTO> findAll();

    UserDTO findById(Long id);

    UserDTO findByEmail(String email);

    boolean existsByEmail(String email);

    UserDTO save(User entity);

    UserDTO create(UserRegistrationDTO entity);

    UserDTO update(UserUpdateDTO entity);

    void deleteById(Long id);

    long countAll();
}
