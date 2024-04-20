package com.lebedev.exchangeRate.controllers;

import com.lebedev.exchangeRate.service.ScheduleRetestChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RestEndpointTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScheduleRetestChecker scheduleRetestChecker;

    @BeforeEach
    public void setUp() {
        Mockito.when(scheduleRetestChecker.checkUSDToRUBRate()).thenReturn("100");
    }

    @Test
    public void testUsdToRubEndpoint() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/rates/usd/rub").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"result\":\"success\",\"value\":\"100\"}")));
    }

}
