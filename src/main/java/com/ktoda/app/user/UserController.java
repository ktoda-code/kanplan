package com.ktoda.app.user;

import com.ktoda.app.user.dto.UserDTO;
import com.ktoda.app.user.dto.UserRegistrationDTO;
import com.ktoda.app.user.dto.UserUpdateDTO;
import com.ktoda.app.user.exception.UserAlreadyExistsException;
import com.ktoda.app.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping("/users")
    public String listAll(Model model,
                          @RequestParam(required = false, defaultValue = "id") String sortBy,
                          @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                          @RequestParam(required = false, defaultValue = "0") int pageNumber,
                          @RequestParam(required = false, defaultValue = "3") int pageElements,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        if (pageElements < 1) {
            pageElements = 3;
        }

        if (!Arrays.asList("ASC", "DESC").contains(sortDir.toUpperCase())) {
            return "user/users";
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir.toUpperCase()), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageElements, sort);
        Page<UserDTO> page = service.findAllPage(pageable);
        List<UserDTO> users = page.getContent();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        response.setHeader("X-Total-Pages", Integer.toString(page.getTotalPages()));

        model.addAttribute("users", users);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("countUsers", service.countAll());
        model.addAttribute("pageElements", pageElements);

        if (isAjax) {
            return "user/fragments/userTable";
        } else {
            return "user/users";
        }
    }

    @GetMapping("/users/{id}")
    public String userDetails(Model model,
                              @PathVariable
                              long id) {
        UserDTO userDTO;
        try {
            userDTO = service.findById(id);
        } catch (UserNotFoundException unf) {
            model.addAttribute("errors", "User not found");
            return "user/userDetails";
        }
        model.addAttribute("user", userDTO);
        model.addAttribute("updatableUser", new UserUpdateDTO(id, userDTO.email(), null));
        return "user/userDetails";
    }

    @GetMapping("/users/create")
    public String createUserView(Model model) {
        model.addAttribute("user", new UserRegistrationDTO(null, null));
        return "user/userCreate";
    }

    @PostMapping("/users/create")
    public String createUser(Model model,
                             @Valid @ModelAttribute("user") UserRegistrationDTO registrationDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", "Errors creating user");
            return "user/userCreate";
        }

        try {
            service.create(registrationDTO);
        } catch (UserAlreadyExistsException uae) {
            model.addAttribute("errors", "User Already exists with this email");
            return "user/userCreate";
        }

        redirectAttributes.addFlashAttribute("success", "User registered successfully!");
        return "redirect:/users";
    }

    @PostMapping("/users/process/{id}")
    public String processUser(
            @PathVariable("id") Long userId,
            @RequestParam("action") String action,
            @Valid @ModelAttribute("updatableUser") UserUpdateDTO updateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if ("delete".equals(action)) {
            try {
                service.deleteById(userId);
                redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            } catch (UserNotFoundException unf) {
                redirectAttributes.addFlashAttribute("errors", "User not found");
            }
            return "redirect:/users";
        } else if ("update".equals(action)) {
            if (bindingResult.hasErrors()) {
                // Redirect to the GET controller to handle setting model attributes
                redirectAttributes.addFlashAttribute("errors", "Error updating user");
                return "redirect:/users/" + userId;
            }
            try {
                service.update(updateDTO);
                redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            } catch (UserNotFoundException unf) {
                redirectAttributes.addFlashAttribute("errors", "User not found");
                return "redirect:/users/" + userId;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errors", "Error updating user");
                return "redirect:/users/" + userId;
            }
            return "redirect:/users/" + userId;
        }
        // In case the action is not recognized
        redirectAttributes.addFlashAttribute("errors", "Unrecognized action");
        return "redirect:/users";
    }


}
