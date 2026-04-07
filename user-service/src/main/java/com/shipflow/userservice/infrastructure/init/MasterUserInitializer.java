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

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class MasterUserInitializer {

    private final UserRepository userRepository;

    @Bean
    @Transactional
    public ApplicationRunner initMasterUser() {
        return args -> {
            String username = "master";
            if (userRepository.findByUsername(username).isPresent()) {
                return;
            }
            UUID masterId = UUID.fromString("0c6a758d-afe4-47a4-9f09-df82c6e99653");
            User master = new User(masterId, "master", "master", "master-admin", UserRole.MASTER, UserStatus.APPROVED);
            userRepository.save(master);
        };
    }

    @Transactional
    public void createMasterUserIfNotExists() {
        String username = "master";

        boolean exists = userRepository.findByUsername(username).isPresent();
        if (exists) {
            return;
        }

        UUID masterId = UUID.fromString("0c6a758d-afe4-47a4-9f09-df82c6e99653");

        User master = new User(masterId, "master", "master", "master-admin", UserRole.MASTER, UserStatus.APPROVED);
        userRepository.save(master);
    }
}