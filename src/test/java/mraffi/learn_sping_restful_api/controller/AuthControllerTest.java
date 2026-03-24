package mraffi.learn_sping_restful_api.controller;

import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.helper.UserTestFactory;
import mraffi.learn_sping_restful_api.model.request.LoginRequest;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.auth.LoginResponse;
import mraffi.learn_sping_restful_api.repository.ContactRepository;
import mraffi.learn_sping_restful_api.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    void testLoginValidationError() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("")
                .password("")
                .build();

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<LoginResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<LoginResponse>>() {
                            }
                    );

                    assertEquals("Validation Failed", response.getMessage());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("username"));
                    assertTrue(response.getErrors().containsKey("password"));

                    assertTrue(response.getErrors().get("username").contains("Username is required"));
                    assertTrue(response.getErrors().get("password").contains("Password is required"));
                });

    }

    @Test
    void testLoginUsernameWrong() throws Exception {
        User user = UserTestFactory.createUser();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .username("salah")
                .password("password")
                .build();

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                   WebResponse<LoginResponse> response = objectMapper.readValue(
                           result.getResponse().getContentAsString(),
                           new TypeReference<WebResponse<LoginResponse>>() {
                           }
                   );

                   assertEquals("Business Error", response.getMessage());
                   assertEquals("LOGIN_FAILED", response.getCode());
                   assertNotNull(response.getErrors());

                   assertTrue(response.getErrors().containsKey("global"));
                   assertTrue(response.getErrors().get("global").contains("Username or password wrong"));
                });
    }

    @Test
    void testLoginPasswordWrong() throws Exception {
        User user = UserTestFactory.createUser();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .username("test")
                .password("salah")
                .build();

        mockMvc.perform(
                        post("/api/auth/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<LoginResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<LoginResponse>>() {
                            }
                    );

                    assertEquals("Business Error", response.getMessage());
                    assertEquals("LOGIN_FAILED", response.getCode());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("global"));
                    assertTrue(response.getErrors().get("global").contains("Username or password wrong"));
                });
    }

    @Test
    void testLoginSuccess() throws Exception {
        User user = UserTestFactory.createUser();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .username("test-username")
                .password("password")
                .build();

        mockMvc.perform(
                        post("/api/auth/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<LoginResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<LoginResponse>>() {
                            }
                    );

                    assertEquals("Login Success", response.getMessage());
                    assertNull(response.getErrors());
                    assertNotNull(response.getData().getToken());
                    assertNotNull(response.getData().getExpiredAt());

                    User userDb = userRepository.findById("test-username").orElse(null);
                    assertNotNull(userDb);
                    assertEquals(userDb.getToken(), response.getData().getToken());
                    assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
                });
    }

    @Test
    void testLogoutUnauthorizedTokenNotSend() throws Exception {
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );

                    assertEquals("Business Error", response.getMessage());
                    assertEquals("UNAUTHORIZED", response.getCode());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("global"));
                    assertTrue(response.getErrors().get("global").contains("Unauthorized"));
                });
    }

    @Test
    void testLogoutUnauthorizedTokenWrong() throws Exception {
        mockMvc.perform(
                        delete("/api/auth/logout")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "wrong")
                ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );

                    assertEquals("Business Error", response.getMessage());
                    assertEquals("UNAUTHORIZED", response.getCode());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("global"));
                    assertTrue(response.getErrors().get("global").contains("Unauthorized"));
                });
    }

    @Test
    void testLogoutUnauthorizedTokenExpired() throws Exception {
        User user = UserTestFactory.createExpiredUser();
        userRepository.save(user);

        mockMvc.perform(
                        delete("/api/auth/logout")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "token")
                ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );

                    assertEquals("Business Error", response.getMessage());
                    assertEquals("TOKEN_EXPIRED", response.getCode());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("global"));
                    assertTrue(response.getErrors().get("global").contains("Unauthorized"));
                });
    }

    @Test
    void testLogoutSuccess() throws Exception {
        User user = UserTestFactory.createUserWithToken();
        userRepository.save(user);

        mockMvc.perform(
                        delete("/api/auth/logout")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "token")
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<String>>() {
                            }
                    );

                    assertEquals("Logout Success", response.getMessage());
                    assertNull(response.getErrors());

                    User userDb = userRepository.findById("test-username").orElse(null);
                    assertNotNull(userDb);
                    assertNull(userDb.getToken());
                    assertNull(userDb.getTokenExpiredAt());
                });
    }


}