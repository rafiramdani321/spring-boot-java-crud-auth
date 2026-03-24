package mraffi.learn_sping_restful_api.controller;

import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.helper.UserTestFactory;
import mraffi.learn_sping_restful_api.model.request.RegisterRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateUserRequest;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.user.RegisterResponse;
import mraffi.learn_sping_restful_api.model.response.user.UserResponse;
import mraffi.learn_sping_restful_api.repository.ContactRepository;
import mraffi.learn_sping_restful_api.repository.UserRepository;
import mraffi.learn_sping_restful_api.security.BCrypt;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("test")
                .password("password")
                .name("test")
                .build();

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<RegisterResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<RegisterResponse>>() {
                            }
                    );

                    assertEquals("Register Success", response.getMessage());
                    assertEquals("test", response.getData().getUsername());
                    assertEquals("test", response.getData().getName());

                    assertNull(response.getErrors());
                    assertTrue(userRepository.existsById("test"));
                });
    }

    @Test
    void testRegisterValidationError() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("")
                .password("")
                .name("")
                .build();

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<RegisterResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<RegisterResponse>>() {
                            }
                    );

                    assertEquals("Validation Failed", response.getMessage());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("username"));
                    assertTrue(response.getErrors().containsKey("password"));
                    assertTrue(response.getErrors().containsKey("name"));

                    assertTrue(response.getErrors().get("username").contains("Username is required"));
                    assertTrue(response.getErrors().get("name").contains("Name is required"));
                    assertTrue(response.getErrors().get("password").contains("Password is required"));
                    assertTrue(response.getErrors().get("password").contains("Password length must be between 4 and 100"));
                });

    }

    @Test
    void testRegisterDuplicateError() throws Exception {
        User user = UserTestFactory.createUser();
        user.setUsername("test");
        userRepository.save(user);

        RegisterRequest request = RegisterRequest.builder()
                .username("test")
                .password("password")
                .name("test")
                .build();

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<RegisterResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<RegisterResponse>>() {
                            }
                    );

                    assertEquals("Business Error", response.getMessage());
                    assertEquals("USER_ALREADY_EXIST", response.getCode());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("username"));
                    assertTrue(response.getErrors().get("username").contains("Username already registered"));
                });
    }

    @Test
    void testGetUserUnauthorizedTokenWrong() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "not-found")
        ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
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
    void testGetUserUnauthorizedTokenNotSend() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>(){}
                    );

                    assertEquals("Business Error", response.getMessage());
                    assertEquals("UNAUTHORIZED", response.getCode());
                    assertNotNull(response.getErrors());

                    assertTrue(response.getErrors().containsKey("global"));
                    assertTrue(response.getErrors().get("global").contains("Unauthorized"));
                });
    }

    @Test
    void testGetUserTokenExpired() throws Exception {
        User user = UserTestFactory.createExpiredUser();
        userRepository.save(user);

        mockMvc.perform(
                        get("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "token")
                ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
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
    void testGetUserSuccess() throws Exception {
        User user = UserTestFactory.createUserWithToken();
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "token")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
                            }
                    );

                    assertEquals("Fetching Get User Success", response.getMessage());
                    assertNull(response.getErrors());
                    assertNotNull(response.getData().getUsername());
                    assertNotNull(response.getData().getName());
                    assertEquals("test-username", response.getData().getUsername());
                    assertEquals("test-name", response.getData().getName());
                });
    }

    @Test
    void testUpdateUserUnauthorizedTokenWrong() throws  Exception {
        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "not-found")
        ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
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
    void testUpdateUserUnauthorizedTokenNotSend() throws  Exception {
        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                        patch("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
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
    void testUpdateUserTokenExpired() throws Exception {
        User user = UserTestFactory.createExpiredUser();
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                        patch("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("X-API-TOKEN", "token")
                ).andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
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
    void testUpdateUserSuccess() throws Exception {
        User user = UserTestFactory.createUserWithToken();
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("rafi ramdani")
                .password("rafi123")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "token")
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<UserResponse>>() {
                            }
                    );

                    assertEquals("Update Success", response.getMessage());
                    assertNull(response.getErrors());
                    assertNotNull(response.getData().getUsername());
                    assertNotNull(response.getData().getName());
                    assertEquals("test-username", response.getData().getUsername());
                    assertEquals("rafi ramdani", response.getData().getName());

                    User userdb = userRepository.findById("test-username").orElse(null);
                    assertNotNull(userdb);
                    assertTrue(BCrypt.checkpw("rafi123", userdb.getPassword()));
                });
    }
}