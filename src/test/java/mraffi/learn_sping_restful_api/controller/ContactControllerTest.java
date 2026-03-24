package mraffi.learn_sping_restful_api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mraffi.learn_sping_restful_api.entity.Contact;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.helper.ContactTestFactory;
import mraffi.learn_sping_restful_api.helper.UserTestFactory;
import mraffi.learn_sping_restful_api.model.request.CreateContactRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateContactRequest;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.contact.ContactResponse;
import mraffi.learn_sping_restful_api.repository.ContactRepository;
import mraffi.learn_sping_restful_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ContactRepository contactRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private ObjectMapper objectMapper;

   @BeforeEach
   void setUp(){
      contactRepository.deleteAll();
      userRepository.deleteAll();
   }

   @Test
   void testCreateContactUnauthorized() throws Exception{
      CreateContactRequest request = CreateContactRequest.builder()
              .firstName("rafi")
              .email("example@gmail.com")
              .build();

      mockMvc.perform(
              post("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testCreateContactUnauthorizedTokenNotSend() throws Exception{
      CreateContactRequest request = CreateContactRequest.builder()
              .firstName("rafi")
              .email("example@gmail.com")
              .build();

      mockMvc.perform(
                      post("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testCreateContactUnauthorizedTokenExpired() throws Exception{
      User user = UserTestFactory.createExpiredUser();
      userRepository.save(user);

      CreateContactRequest request = CreateContactRequest.builder()
              .firstName("rafi")
              .email("example@gmail.com")
              .build();

      mockMvc.perform(
                      post("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("TOKEN_EXPIRED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testCreateContactValidationError() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      CreateContactRequest request = CreateContactRequest.builder()
              .firstName("")
              .email("salah")
              .build();

      mockMvc.perform(
                      post("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
                              .header("X-API-TOKEN", "token")
              ).andExpect  (status().isBadRequest())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Validation Failed", response.getMessage());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("firstName"));
                 assertTrue(response.getErrors().containsKey("email"));

                 assertTrue(response.getErrors().get("firstName").contains("Firstname is required"));
                 assertTrue(response.getErrors().get("email").contains("Email not valid"));
              });

   }

   @Test
   void testCreateContactSuccess() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      CreateContactRequest request = CreateContactRequest.builder()
              .firstName("rafi")
              .lastName("ramdani")
              .email("example@gmail.com")
              .phone("0812467456")
              .build();

      mockMvc.perform(
                      post("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Create Contact Success", response.getMessage());
                 assertNull(response.getErrors());

                 assertNotNull(response.getData().getFirstName());
                 assertNotNull(response.getData().getLastName());
                 assertNotNull(response.getData().getEmail());
                 assertNotNull(response.getData().getPhone());

                 assertEquals("rafi", response.getData().getFirstName());
                 assertEquals("ramdani", response.getData().getLastName());
                 assertEquals("example@gmail.com", response.getData().getEmail());
                 assertEquals("0812467456", response.getData().getPhone());

                 assertTrue(contactRepository.existsById(response.getData().getId()));
              });

   }

   @Test
   void testGetContactUnauthorized() throws Exception{
      mockMvc.perform(
                      get("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "wrong")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testGetContactUnauthorizedTokenNotSend() throws Exception{
      mockMvc.perform(
                      get("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testGetContactUnauthorizedTokenExpired() throws Exception{
      User user = UserTestFactory.createExpiredUser();
      userRepository.save(user);

      mockMvc.perform(
                      get("/api/contacts/test-username")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("TOKEN_EXPIRED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testGetContactNotFound() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      mockMvc.perform(
                      get("/api/contacts/not-found")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isNotFound())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("CONTACT_NOT_FOUND", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Contact not found"));
              });

   }

   @Test
   void testGetContactSuccess() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      Contact contact = ContactTestFactory.createContact(user);
      contactRepository.save(contact);

      mockMvc.perform(
                      get("/api/contacts/" + contact.getId())
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertNull(response.getErrors());
                 assertEquals("Fetch Data Success", response.getMessage());

                 assertEquals(contact.getId(), response.getData().getId());
                 assertEquals(contact.getFirstName(), response.getData().getFirstName());
                 assertEquals(contact.getLastName(), response.getData().getLastName());
                 assertEquals(contact.getEmail(), response.getData().getEmail());
                 assertEquals(contact.getPhone(), response.getData().getPhone());
              });

   }

   @Test
   void testUpdateContactUnauthorized() throws Exception{
      mockMvc.perform(
                      put("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testUpdateContactUnauthorizedTokenNotSend() throws Exception{
      mockMvc.perform(
                      put("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testUpdateContactUnauthorizedTokenExpired() throws Exception{
      User user = UserTestFactory.createExpiredUser();
      userRepository.save(user);

      mockMvc.perform(
                      post("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("TOKEN_EXPIRED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testUpdateContactValidationError() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      UpdateContactRequest request = new UpdateContactRequest();
      request.setFirstName("");
      request.setEmail("wrong");

      mockMvc.perform(
                      put("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
                              .header("X-API-TOKEN", "token")
              ).andExpect  (status().isBadRequest())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Validation Failed", response.getMessage());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("firstName"));
                 assertTrue(response.getErrors().containsKey("email"));

                 assertTrue(response.getErrors().get("firstName").contains("Firstname is required"));
                 assertTrue(response.getErrors().get("email").contains("Email not valid"));
              });

   }

   @Test
   void testUpdateContactNotFound() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      UpdateContactRequest request = new UpdateContactRequest();
      request.setFirstName("rafi");
      request.setEmail("example@gmail.com");

      mockMvc.perform(
                      put("/api/contacts/not-found")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isNotFound())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("CONTACT_NOT_FOUND", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Contact not found"));
              });

   }

   @Test
   void testUpdateContactSuccess() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      Contact contact = ContactTestFactory.createContact(user);
      contactRepository.save(contact);

      UpdateContactRequest request = UpdateContactRequest.builder()
              .firstName("rafi update")
              .lastName("ramdani")
              .email("rafi@gmail.com")
              .phone("0812467456")
              .build();

      mockMvc.perform(
                      put("/api/contacts/" + contact.getId())
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Update Contact Success", response.getMessage());
                 assertNull(response.getErrors());

                 assertNotNull(response.getData().getFirstName());
                 assertNotNull(response.getData().getLastName());
                 assertNotNull(response.getData().getEmail());
                 assertNotNull(response.getData().getPhone());

                 assertEquals("rafi update", response.getData().getFirstName());
                 assertEquals("ramdani", response.getData().getLastName());
                 assertEquals("rafi@gmail.com", response.getData().getEmail());
                 assertEquals("0812467456", response.getData().getPhone());

                 assertTrue(contactRepository.existsById(response.getData().getId()));
              });
   }

   @Test
   void testDeleteContactUnauthorized() throws Exception{
      mockMvc.perform(
                      delete("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testDeleteContactUnauthorizedTokenNotSend() throws Exception{
      mockMvc.perform(
                      delete("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testDeleteContactUnauthorizedTokenExpired() throws Exception{
      User user = UserTestFactory.createExpiredUser();
      userRepository.save(user);

      mockMvc.perform(
                      delete("/api/contacts/test")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("TOKEN_EXPIRED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testDeleteContactNotFound() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      mockMvc.perform(
                      delete("/api/contacts/not-found")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isNotFound())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("CONTACT_NOT_FOUND", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Contact not found"));
              });

   }

   @Test
   void testDeleteContactSuccess() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      Contact contact = ContactTestFactory.createContact(user);
      contactRepository.save(contact);

      mockMvc.perform(
                      delete("/api/contacts/" + contact.getId())
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<ContactResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<ContactResponse>>() {}
                 );

                 assertEquals("Delete Contact Success", response.getMessage());
                 assertNull(response.getErrors());

                 assertFalse(contactRepository.existsById(contact.getId()));
              });
   }

   @Test
   void testSearchContactUnauthorized() throws Exception{
      mockMvc.perform(
                      get("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "wrong")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testSearchContactUnauthorizedTokenNotSend() throws Exception{
      mockMvc.perform(
                      get("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("UNAUTHORIZED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testSeacrhContactUnauthorizedTokenExpired() throws Exception{
      User user = UserTestFactory.createExpiredUser();
      userRepository.save(user);

      mockMvc.perform(
                      get("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isUnauthorized())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertEquals("Business Error", response.getMessage());
                 assertEquals("TOKEN_EXPIRED", response.getCode());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Unauthorized"));
              });

   }

   @Test
   void testSearchContactNotFound() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      mockMvc.perform(
                      get("/api/contacts")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertNull(response.getErrors());
                 assertEquals(0, response.getData().size());
                 assertEquals(0, response.getPaging().getTotalPage());
                 assertEquals(0, response.getPaging().getCurrentPage());
                 assertEquals(10, response.getPaging().getSize());
              });

   }

   @Test
   void testSearchContactSuccess() throws Exception{
      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      for(int i = 0; i < 100; i++){
         Contact contact = new Contact();
         contact.setId(UUID.randomUUID().toString());
         contact.setUser(user);
         contact.setFirstName("Rafi " + i);
         contact.setLastName("ramdani");
         contact.setEmail("rafi@example.com");
         contact.setPhone("0814756476595");
         contactRepository.save(contact);
      }

      mockMvc.perform(
                      get("/api/contacts")
                              .queryParam("name", "Rafi")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertNull(response.getErrors());
                 assertEquals(10, response.getData().size());
                 assertEquals(10, response.getPaging().getTotalPage());
                 assertEquals(0, response.getPaging().getCurrentPage());
                 assertEquals(10, response.getPaging().getSize());
              });

      mockMvc.perform(
                      get("/api/contacts")
                              .queryParam("name", "Ramdani")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertNull(response.getErrors());
                 assertEquals(10, response.getData().size());
                 assertEquals(10, response.getPaging().getTotalPage());
                 assertEquals(0, response.getPaging().getCurrentPage());
                 assertEquals(10, response.getPaging().getSize());
              });

      mockMvc.perform(
                      get("/api/contacts")
                              .queryParam("email", "example.com")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertNull(response.getErrors());
                 assertEquals(10, response.getData().size());
                 assertEquals(10, response.getPaging().getTotalPage());
                 assertEquals(0, response.getPaging().getCurrentPage());
                 assertEquals(10, response.getPaging().getSize());
              });

      mockMvc.perform(
                      get("/api/contacts")
                              .queryParam("phone", "76595")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertNull(response.getErrors());
                 assertEquals(10, response.getData().size());
                 assertEquals(10, response.getPaging().getTotalPage());
                 assertEquals(0, response.getPaging().getCurrentPage());
                 assertEquals(10, response.getPaging().getSize());
              });

      mockMvc.perform(
                      get("/api/contacts")
                              .queryParam("phone", "76595")
                              .queryParam("page", "1000")
                              .accept(MediaType.APPLICATION_JSON)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("X-API-TOKEN", "token")
              ).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<List<ContactResponse>>>() {}
                 );

                 assertNull(response.getErrors());
                 assertEquals(0, response.getData().size());
                 assertEquals(10, response.getPaging().getTotalPage());
                 assertEquals(1000, response.getPaging().getCurrentPage());
                 assertEquals(10, response.getPaging().getSize());
              });

   }
}