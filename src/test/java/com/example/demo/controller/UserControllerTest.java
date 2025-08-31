package com.example.demo.controller;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_returnCreatedUser() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(
                "testuser", "test@example.com", 22
        );

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(22))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getUserById_returnUser() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto(
                "testuser", "test@example.com", 22
        );

        MvcResult result = mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andReturn();

        UserDto createdUser = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        mockMvc.perform(get("/api/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_returnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_updateAndReturnUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto(
                "testuser", "test@example.com", 22
        );

        MvcResult result = mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        UserDto createdUser = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(createdUser.getId());
        updateDto.setUsername("updated");
        updateDto.setEmail("updated@example.com");
        updateDto.setAge("23");

        mockMvc.perform(put("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(23));
    }

    @Test
    void deleteUser_deleteUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto(
                "testuser", "test@example.com", 22
        );

        MvcResult result = mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn();

        UserDto createdUser = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        mockMvc.perform(delete("/api/users/")
                        .param("id", createdUser.getId().toString()))
                .andExpect(status().isAccepted());

        mockMvc.perform(get("/api/users/{id}", createdUser.getId()))
                .andExpect(status().isNotFound());
    }
}