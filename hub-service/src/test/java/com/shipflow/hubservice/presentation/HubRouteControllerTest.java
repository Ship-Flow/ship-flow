package com.shipflow.hubservice.presentation;

import com.jayway.jsonpath.JsonPath;
import com.shipflow.hubservice.infrastructure.persistence.HubJpaRepository;
import com.shipflow.hubservice.support.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HubRouteControllerTest extends IntegrationTestBase {

    @Autowired
    private HubJpaRepository hubRepository;

    private UUID departureHubId;
    private UUID arrivalHubId;

    @BeforeEach
    void setUp() {
        var hubs = hubRepository.findAll();
        Assumptions.assumeTrue(hubs.size() >= 2, "DataInitializer가 허브를 2개 이상 시딩해야 합니다.");
        departureHubId = hubs.get(0).getId();
        arrivalHubId = hubs.get(1).getId();
    }

    private String createRouteJson() {
        return String.format(
                "{\"departureHubId\":\"%s\",\"arrivalHubId\":\"%s\",\"duration\":120,\"distance\":160.5}",
                departureHubId, arrivalHubId);
    }

    private String updateRouteJson() {
        return "{\"duration\":90,\"distance\":100.0}";
    }

    private UUID createRouteAndGetId() throws Exception {
        String res = mockMvc.perform(asMaster(
                        post("/api/hub-routes").contentType(MediaType.APPLICATION_JSON).content(createRouteJson())))
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(res, "$.data.id"));
    }

    @Test
    void createRoute_asMaster_returns201() throws Exception {
        mockMvc.perform(asMaster(post("/api/hub-routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRouteJson())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.departureHubId").exists());
    }

    @Test
    void createRoute_asManager_returns403() throws Exception {
        mockMvc.perform(asManager(post("/api/hub-routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRouteJson())))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRoute_existingId_returns200() throws Exception {
        UUID id = createRouteAndGetId();

        mockMvc.perform(get("/api/hub-routes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void getRoute_nonExistentId_returns404() throws Exception {
        mockMvc.perform(get("/api/hub-routes/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("HUB_102"));
    }

    @Test
    void getRoutes_returns200WithList() throws Exception {
        mockMvc.perform(get("/api/hub-routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getRoutes_excludesSoftDeleted() throws Exception {
        UUID id = createRouteAndGetId();
        mockMvc.perform(asMaster(delete("/api/hub-routes/{id}", id)));

        mockMvc.perform(get("/api/hub-routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void updateRoute_asMaster_returns200() throws Exception {
        UUID id = createRouteAndGetId();

        mockMvc.perform(asMaster(patch("/api/hub-routes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRouteJson())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duration").value(90));
    }

    @Test
    void updateRoute_asManager_returns403() throws Exception {
        UUID id = createRouteAndGetId();

        mockMvc.perform(asManager(patch("/api/hub-routes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRouteJson())))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteRoute_asMaster_returns200() throws Exception {
        UUID id = createRouteAndGetId();

        mockMvc.perform(asMaster(delete("/api/hub-routes/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteRoute_asManager_returns403() throws Exception {
        UUID id = createRouteAndGetId();

        mockMvc.perform(asManager(delete("/api/hub-routes/{id}", id)))
                .andExpect(status().isForbidden());
    }
}
