package com.example.crashcourse.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 2) "Slice" test: starts ONLY the web layer (controllers, JSON, validation, @RestControllerAdvice).
// No database. The service is replaced by a Mockito mock that lives in the Spring context.
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc; // sends fake HTTP requests without a real server

    @MockitoBean
    UserService userService;

    @Test
    void returnsUserById() throws Exception {
        when(userService.getById(1L)).thenReturn(new User(1L, "ada", "ada@example.com", "Ada Lovelace"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ada"))
                .andExpect(jsonPath("$.fullName").value("Ada Lovelace"));
    }

    @Test
    void rejectsInvalidUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "", "email": "not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").value("must not be blank"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void returns404ForUnknownUser() throws Exception {
        when(userService.getById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("User 99 not found"));
    }
}
