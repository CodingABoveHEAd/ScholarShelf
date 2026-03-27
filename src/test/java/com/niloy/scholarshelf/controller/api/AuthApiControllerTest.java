package com.niloy.scholarshelf.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niloy.scholarshelf.dto.response.AuthResponse;
import com.niloy.scholarshelf.dto.request.LoginRequest;
import com.niloy.scholarshelf.dto.request.RegisterRequest;
import com.niloy.scholarshelf.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthApiControllerTest.TestApp.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
public class AuthApiControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authService);
    }

    @Test
    void registerUser_ReturnsAuthResponse_WhenValidRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@ex.com");
        registerRequest.setPassword("testpwd");
        registerRequest.setFullName("Test User");
        registerRequest.setRole("BUYER");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("test_jwt_token");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test_jwt_token"));
    }

    @Test
    void loginUser_ReturnsAuthResponse_WhenValidRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@ex.com");
        loginRequest.setPassword("testpwd");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("test_jwt_token");

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test_jwt_token"));
    }

    @Test
    void loginUser_ReturnsBadRequest_WhenMissingEmail() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("testpwd");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({AuthApiController.class, TestBeans.class})
    static class TestApp {
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }
}
