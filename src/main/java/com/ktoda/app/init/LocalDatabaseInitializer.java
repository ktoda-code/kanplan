package com.ktoda.app.init;

import com.ktoda.app.user.User;
import com.ktoda.app.user.UserService;
import com.ktoda.app.user.dto.UserRegistrationDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
@AllArgsConstructor
public class LocalDatabaseInitializer implements CommandLineRunner {
    private final UserService userService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Local database initialization loading...");

        userService.create(new UserRegistrationDTO("alan@kanplan.com", "alan123"));
        userService.create(new UserRegistrationDTO("ktod.andreev@gmail.com", "ktoda123"));
        userService.create(new UserRegistrationDTO("tsonka@kanplan.com", "tsonka123"));
        userService.create(new UserRegistrationDTO("user4@example.com", "password4"));
        userService.create(new UserRegistrationDTO("user5@example.com", "password5"));
        userService.create(new UserRegistrationDTO("user6@example.com", "password6"));
        userService.create(new UserRegistrationDTO("user7@example.com", "password7"));
        userService.create(new UserRegistrationDTO("user8@example.com", "password8"));
        userService.create(new UserRegistrationDTO("user9@example.com", "password9"));
        userService.create(new UserRegistrationDTO("user10@example.com", "password10"));

        log.info("Users loaded: ");
        userService.findAll().forEach(System.out::println);
    }
}
