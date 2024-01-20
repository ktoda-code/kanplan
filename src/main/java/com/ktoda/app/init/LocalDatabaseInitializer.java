package com.ktoda.app.init;

import com.ktoda.app.user.User;
import com.ktoda.app.user.UserService;
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
    public void run(String... args) throws Exception {
        log.info("Local database initialization loading...");

        userService.save(new User("alan@kanplan.com", "alan123"));
        userService.save(new User("ktod.andreev@gmail.com", "ktoda123"));
        userService.save(new User("tsonka@kanplan.com", "tsonka123"));
        userService.save(new User("user4@example.com", "password4"));
        userService.save(new User("user5@example.com", "password5"));
        userService.save(new User("user6@example.com", "password6"));
        userService.save(new User("user7@example.com", "password7"));
        userService.save(new User("user8@example.com", "password8"));
        userService.save(new User("user9@example.com", "password9"));
        userService.save(new User("user10@example.com", "password10"));

        log.info("Users loaded: ");
        userService.findAll().forEach(System.out::println);
    }
}
