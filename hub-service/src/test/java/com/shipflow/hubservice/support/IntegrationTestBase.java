package com.shipflow.hubservice.support;

import com.shipflow.hubservice.infrastructure.client.CompanyClient;
import com.shipflow.hubservice.infrastructure.client.DeliveryClient;
import com.shipflow.hubservice.infrastructure.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserClient userClient;

    @MockBean
    protected DeliveryClient deliveryClient;

    @MockBean
    protected CompanyClient companyClient;

    protected MockHttpServletRequestBuilder asMaster(MockHttpServletRequestBuilder req) {
        return req.header("X-User-Role", "MASTER");
    }

    protected MockHttpServletRequestBuilder asManager(MockHttpServletRequestBuilder req) {
        return req.header("X-User-Role", "HUB_MANAGER");
    }
}
