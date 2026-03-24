package mraffi.learn_sping_restful_api.service;

import lombok.RequiredArgsConstructor;
import mraffi.learn_sping_restful_api.entity.Address;
import mraffi.learn_sping_restful_api.entity.Contact;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.exception.ApiException;
import mraffi.learn_sping_restful_api.model.request.CreateAddressRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateAddressRequest;
import mraffi.learn_sping_restful_api.model.response.address.AddressResponse;
import mraffi.learn_sping_restful_api.repository.AddressRepository;
import mraffi.learn_sping_restful_api.repository.ContactRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

   private final AddressRepository addressRepository;

   private final ContactRepository contactRepository;

   private final ContactService contactService;

   private AddressResponse toAddressResponse(Address address){
      return AddressResponse.builder()
              .id(address.getId())
              .street(address.getStreet())
              .city(address.getCity())
              .province(address.getProvince())
              .country(address.getCountry())
              .postalCode(address.getPostalCode())
              .build();
   }

   private Address toEntity(CreateAddressRequest request, Contact contact){
      return Address.builder()
              .id(UUID.randomUUID().toString())
              .street(request.getStreet())
              .city(request.getCity())
              .province(request.getProvince())
              .country(request.getCountry())
              .postalCode(request.getPostalCode())
              .contact(contact)
              .build();
   }

   private Address findAddressOrThrow(Contact contact, String id){
      return addressRepository.findFirstByContactAndId(contact, id)
              .orElseThrow(() -> new ApiException(
                      "ADDRESS_NOT_FOUND",
                      HttpStatus.NOT_FOUND,
                      "global",
                      "Address not found"
              ));
   }

   @Transactional
   public AddressResponse create(User user, CreateAddressRequest request){
      Contact contact = contactService.getContactEntity(user, request.getContactId());

      Address address = toEntity(request, contact);
      addressRepository.save(address);
      return toAddressResponse(address);
   }

   @Transactional
   public AddressResponse update(User user, UpdateAddressRequest request){
      Contact contact = contactService.getContactEntity(user, request.getContactId());
      Address address = findAddressOrThrow(contact, request.getId());

      address.setStreet(request.getStreet());
      address.setCity(request.getCity());
      address.setProvince(request.getProvince());
      address.setCountry(request.getCountry());
      address.setPostalCode(request.getPostalCode());

      return toAddressResponse(address);
   }

}
