package mraffi.learn_sping_restful_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mraffi.learn_sping_restful_api.entity.Contact;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.model.request.CreateContactRequest;
import mraffi.learn_sping_restful_api.model.request.SearchContactRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateContactRequest;
import mraffi.learn_sping_restful_api.model.response.PagingResponse;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.contact.ContactResponse;
import mraffi.learn_sping_restful_api.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContactController {

   private final ContactService contactService;

   @PostMapping(
           path = "/api/contacts",
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
   )
   @ResponseStatus(HttpStatus.CREATED)
   public WebResponse<ContactResponse> create(User user, @Valid @RequestBody CreateContactRequest request){
      ContactResponse contactResponse = contactService.create(user, request);
      return WebResponse.<ContactResponse>builder()
              .data(contactResponse)
              .message("Create Contact Success")
              .build();
   }

   @GetMapping(
           path = "/api/contacts/{contactId}",
           produces = MediaType.APPLICATION_JSON_VALUE
   )
   public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId){
      ContactResponse contactResponse = contactService.get(user, contactId);
      return WebResponse.<ContactResponse>builder()
              .data(contactResponse)
              .message("Fetch Data Success")
              .build();
   }

   @PutMapping(
           path = "/api/contacts/{contactId}",
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
   )
   public WebResponse<ContactResponse> update(User user, @Valid @RequestBody UpdateContactRequest request, @PathVariable("contactId") String contactId){
      request.setId(contactId);

      ContactResponse contactResponse = contactService.update(user, request);
      return WebResponse.<ContactResponse>builder()
              .data(contactResponse)
              .message("Update Contact Success")
              .build();
   }

   @DeleteMapping(
           path = "/api/contacts/{contactId}",
           produces = MediaType.APPLICATION_JSON_VALUE
   )
   public WebResponse<String> delete(User user, @PathVariable("contactId") String contactId){
      contactService.delete(user, contactId);
      return WebResponse.<String>builder()
              .message("Delete Contact Success")
              .build();
   }

   @GetMapping(
           path = "/api/contacts",
           produces = MediaType.APPLICATION_JSON_VALUE
   )
   public WebResponse<List<ContactResponse>> search(User user,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "email", required = false) String email,
                                                    @RequestParam(value = "phone", required = false) String phone,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){

      SearchContactRequest request = SearchContactRequest.builder()
              .page(page)
              .size(size)
              .name(name)
              .email(email)
              .phone(phone)
              .build();

      Page<ContactResponse> contactResponses = contactService.search(user, request);
      return WebResponse.<List<ContactResponse>>builder()
              .message("Get search contact success")
              .data(contactResponses.getContent())
              .paging(PagingResponse.builder()
                      .currentPage(contactResponses.getNumber())
                      .totalPage(contactResponses.getTotalPages())
                      .size(contactResponses.getSize())
                      .build())
              .build();
   }

}
