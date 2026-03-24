package mraffi.learn_sping_restful_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.model.request.CreateAddressRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateAddressRequest;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.address.AddressResponse;
import mraffi.learn_sping_restful_api.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AddressController {

   private final AddressService addressService;

   @PostMapping(
           path = "/api/contacts/{contactId}/addresses",
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
   )
   @ResponseStatus(HttpStatus.CREATED)
   public WebResponse<AddressResponse> create(User user, @Valid @RequestBody CreateAddressRequest request, @PathVariable("contactId") String contactId){
      request.setContactId(contactId);

      AddressResponse addressResponse = addressService.create(user, request);
      return WebResponse.<AddressResponse>builder()
              .data(addressResponse)
              .message("Create Address Success")
              .build();
   }

   @PostMapping(
           path = "/api/contacts/{contactId}/addresses/{addressId}",
           consumes = MediaType.APPLICATION_JSON_VALUE,
           produces = MediaType.APPLICATION_JSON_VALUE
   )
   public WebResponse<AddressResponse> update(User user,
                                              @Valid @RequestBody UpdateAddressRequest request,
                                              @PathVariable("contactId") String contactId,
                                              @PathVariable("addressId") String addressId){
      request.setContactId(contactId);
      request.setId(addressId);

      AddressResponse addressResponse = addressService.update(user, request);
      return WebResponse.<AddressResponse>builder()
              .data(addressResponse)
              .message("Update Address Success")
              .build();
   }

}
