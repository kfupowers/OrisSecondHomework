package ru.kpfu.itis.shakirov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.shakirov.dto.UserDto;
import ru.kpfu.itis.shakirov.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UserController.class)
@WithMockUser(username = "user", roles = "USER")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnUsersViewWithUsersAttribute() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setUsername("user1");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("user2");
        List<UserDto> mockUsers = List.of(user1, user2);
        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", mockUsers));
    }

    @Test
    void addUser_shouldCreateUserAndReturnLoginView() throws Exception {
        UserDto createdUser = new UserDto();
        createdUser.setUsername("testUser");
        createdUser.setEmail("test@example.com");
        when(userService.createUser("testUser", "test@example.com", "password123"))
                .thenReturn(createdUser);

        mockMvc.perform(post("/users/add")
                        .param("username", "testUser")
                        .param("password", "password123")
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", createdUser));
    }

    @Test
    void getUserById_shouldReturnUserViewWithUserAttribute() throws Exception {
        Long userId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setUsername("John");
        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/edit/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    void updateUser_shouldUpdateAndReturnBoolView() throws Exception {
        Long userId = 1L;
        String newUsername = "UpdatedName";
        when(userService.updateUser(userId, newUsername)).thenReturn(true);

        mockMvc.perform(post("/users/update/{id}", userId)
                        .param("username", newUsername)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("bool"))
                .andExpect(model().attributeExists("bool"))
                .andExpect(model().attribute("bool", true));
    }

    @Test
    void deleteUser_shouldCallServiceAndReturnOk() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/delete/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}