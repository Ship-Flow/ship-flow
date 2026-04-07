package com.shipflow.userservice.infrastructure.init;

import com.shipflow.userservice.domain.entity.User;
import com.shipflow.userservice.domain.model.UserRole;
import com.shipflow.userservice.domain.model.UserStatus;
import com.shipflow.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class MasterUserInitializer {

    private final UserRepository userRepository;

    @Bean
    public ApplicationRunner initMasterUser() {
        return args -> createMasterUserIfNotExists();
    }

    @Transactional
    public void createMasterUserIfNotExists() {
        String username = "master";

        boolean exists = userRepository.findByUsername(username).isPresent();
        if (exists) {
            return;
        }

        UUID masterId = UUID.fromString("0c6a758d-afe4-47a4-9f09-df82c6e99653");
        LocalDateTime now = LocalDateTime.now();

        User master = new User(masterId, "master", "master", "master-admin", UserRole.MASTER, UserStatus.APPROVED);
        userRepository.save(master);
    }
}