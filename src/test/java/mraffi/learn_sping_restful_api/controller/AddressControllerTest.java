package mraffi.learn_sping_restful_api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mraffi.learn_sping_restful_api.entity.Contact;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.helper.ContactTestFactory;
import mraffi.learn_sping_restful_api.helper.UserTestFactory;
import mraffi.learn_sping_restful_api.model.request.CreateAddressRequest;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.address.AddressResponse;
import mraffi.learn_sping_restful_api.repository.AddressRepository;
import mraffi.learn_sping_restful_api.repository.ContactRepository;
import mraffi.learn_sping_restful_api.repository.UserRepository;
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
class AddressControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private AddressRepository addressRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private ContactRepository contactRepository;

   @Autowired
   private ObjectMapper objectMapper;

   @BeforeEach
   void setUp(){
      addressRepository.deleteAll();
      contactRepository.deleteAll();
      userRepository.deleteAll();

      User user = UserTestFactory.createUserWithToken();
      userRepository.save(user);

      Contact contact = ContactTestFactory.createContact(user);
      contact.setId("test");
      contactRepository.save(contact);
   }

   @Test
   void testCreateAddressFailedContactNotFound() throws Exception {
      CreateAddressRequest request = CreateAddressRequest.builder()
              .country("Jakarta").build();

      mockMvc.perform(
              post("/api/contacts/not-found/addresses")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request))
                      .header("X-API-TOKEN", "token")
      ).andExpect(status().isNotFound())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<AddressResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<AddressResponse>>() {
                         }
                 );

                 assertNotNull(response.getErrors());
                 assertEquals("Business Error", response.getMessage());
                 assertEquals("CONTACT_NOT_FOUND", response.getCode());

                 assertTrue(response.getErrors().containsKey("global"));
                 assertTrue(response.getErrors().get("global").contains("Contact not found"));
              });
   }

   @Test
   void testCreateAddressBadRequest() throws Exception {
      CreateAddressRequest request = CreateAddressRequest.builder()
                      .country("").build();

      mockMvc.perform(
              post("/api/contacts/test/addresses")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request))
                      .header("X-API-TOKEN", "token")
      ).andExpect(status().isBadRequest())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<AddressResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<AddressResponse>>() {
                         }
                 );

                 assertEquals("Validation Failed", response.getMessage());
                 assertNotNull(response.getErrors());

                 assertTrue(response.getErrors().containsKey("country"));
                 assertTrue(response.getErrors().get("country").contains("Country is required"));
              });
   }

   @Test
   void testCreateAddressSuccess() throws Exception {
      CreateAddressRequest request = CreateAddressRequest.builder()
              .street("Jalan")
              .city("Jakarta")
              .province("DKI")
              .country("Indonesia")
              .postalCode("123")
              .build();

      mockMvc.perform(
              post("/api/contacts/test/addresses")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request))
                      .header("X-API-TOKEN", "token")
      ).andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andDo(result -> {
                 WebResponse<AddressResponse> response = objectMapper.readValue(
                         result.getResponse().getContentAsString(),
                         new TypeReference<WebResponse<AddressResponse>>() {
                         }
                 );

                 assertNull(response.getErrors());
                 assertEquals("Create Address Success", response.getMessage());

                 assertEquals(request.getStreet(), response.getData().getStreet());
                 assertEquals(request.getCity(), response.getData().getCity());
                 assertEquals(request.getProvince(), response.getData().getProvince());
                 assertEquals(request.getCountry(), response.getData().getCountry());
                 assertEquals(request.getPostalCode(), response.getData().getPostalCode());

                 assertTrue(addressRepository.existsById(response.getData().getId()));

              });
   }

}