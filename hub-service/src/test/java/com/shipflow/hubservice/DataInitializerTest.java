package com.shipflow.hubservice;

import com.shipflow.hubservice.infrastructure.persistence.HubJpaRepository;
import com.shipflow.hubservice.infrastructure.persistence.HubRouteJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DataInitializerTest {

    @Autowired
    private HubJpaRepository hubRepository;

    @Autowired
    private HubRouteJpaRepository hubRouteRepository;

    @Test
    void hubCount_shouldBe17() {
        assertThat(hubRepository.count()).isEqualTo(17);
    }

    @Test
    void routeCount_shouldBePositive() {
        assertThat(hubRouteRepository.count()).isGreaterThan(0);
    }
}
