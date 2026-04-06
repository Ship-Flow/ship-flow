package com.shipflow.hubservice.presentation;

import com.jayway.jsonpath.JsonPath;
import com.shipflow.hubservice.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HubControllerTest extends IntegrationTestBase {

    private String createHubJson() {
        return """
                {
                  "name": "테스트 허브",
                  "address": "서울특별시 강남구 테헤란로 1",
                  "latitude": 37.4981,
                  "longitude": 127.0276,
                  "managerId": "00000000-0000-0000-0000-000000000001",
                  "managerName": "테스트 관리자"
                }
                """;
    }

    private String updateHubJson() {
        return """
                {
                  "managerId": "00000000-0000-0000-0000-000000000002",
                  "managerName": "변경 관리자"
                }
                """;
    }

    private UUID createHubAndGetId() throws Exception {
        String response = mockMvc.perform(asMaster(
                        post("/api/hubs").contentType(MediaType.APPLICATION_JSON).content(createHubJson())))
                .andReturn().getResponse().getContentAsString();
        String idStr = JsonPath.read(response, "$.data.id");
        return UUID.fromString(idStr);
    }

    @Test
    void createHub_asMaster_returns201() throws Exception {
        mockMvc.perform(asMaster(post("/api/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createHubJson())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("테스트 허브"));
    }

    @Test
    void createHub_asManager_returns403() throws Exception {
        mockMvc.perform(asManager(post("/api/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createHubJson())))
                .andExpect(status().isForbidden());
    }

    @Test
    void getHub_existingId_returns200() throws Exception {
        UUID id = createHubAndGetId();

        mockMvc.perform(get("/api/hubs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void getHub_nonExistentId_returns404() throws Exception {
        mockMvc.perform(get("/api/hubs/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("HUB_101"));
    }

    @Test
    void getHubs_returns200WithList() throws Exception {
        mockMvc.perform(get("/api/hubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getHubs_excludesSoftDeleted() throws Exception {
        UUID id = createHubAndGetId();
        mockMvc.perform(asMaster(delete("/api/hubs/{id}", id)));

        mockMvc.perform(get("/api/hubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void updateHub_asMaster_returns200() throws Exception {
        UUID id = createHubAndGetId();

        mockMvc.perform(asMaster(patch("/api/hubs/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateHubJson())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.managerName").value("변경 관리자"));
    }

    @Test
    void updateHub_asManager_returns403() throws Exception {
        UUID id = createHubAndGetId();

        mockMvc.perform(asManager(patch("/api/hubs/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateHubJson())))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateHub_nonExistentId_returns404() throws Exception {
        mockMvc.perform(asMaster(patch("/api/hubs/00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateHubJson())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteHub_asMaster_returns200() throws Exception {
        UUID id = createHubAndGetId();

        mockMvc.perform(asMaster(delete("/api/hubs/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteHub_asManager_returns403() throws Exception {
        UUID id = createHubAndGetId();

        mockMvc.perform(asManager(delete("/api/hubs/{id}", id)))
                .andExpect(status().isForbidden());
    }
}
