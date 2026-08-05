package com.linkup.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkup.ai.dto.LinkedInRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LinkedInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFastReplyEndpoint() throws Exception {

        LinkedInRequest request = new LinkedInRequest();
        request.setMessageContent("Hi, can we connect?");
        request.setTone("Professional");

        mockMvc.perform(post("/api/linkedin/fast-reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").exists())
                .andExpect(jsonPath("$.tone").exists())
                .andExpect(jsonPath("$.action").exists());
    }
}