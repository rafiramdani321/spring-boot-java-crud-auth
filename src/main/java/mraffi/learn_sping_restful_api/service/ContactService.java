package mraffi.learn_sping_restful_api.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mraffi.learn_sping_restful_api.entity.Contact;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.exception.ApiException;
import mraffi.learn_sping_restful_api.model.request.CreateContactRequest;
import mraffi.learn_sping_restful_api.model.request.SearchContactRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateContactRequest;
import mraffi.learn_sping_restful_api.model.response.contact.ContactResponse;
import mraffi.learn_sping_restful_api.repository.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    private ContactResponse toContactResponse(Contact contact){
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();

    }

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request){
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    private Contact findContactOrThrow(User user, String id){
        return contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ApiException(
                        "CONTACT_NOT_FOUND",
                        HttpStatus.NOT_FOUND,
                        "global",
                        "Contact not found"
                ));
    }

    public Contact getContactEntity(User user, String id){
        return findContactOrThrow(user, id);
    }

    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id){
        Contact contact = findContactOrThrow(user, id);
        return toContactResponse(contact);
    }

    @Transactional
    public ContactResponse update(User user, UpdateContactRequest request){
        Contact contact = findContactOrThrow(user, request.getId());

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    @Transactional
    public void delete(User user, String contactId){
        Contact contact = findContactOrThrow(user, contactId);
        contactRepository.delete(contact);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request){
        Specification<Contact> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));

            if(Objects.nonNull(request.getName())){
                predicates.add(builder.or(
                        builder.like(root.get("firstName"), "%" + request.getName() + "%"),
                        builder.like(root.get("lastName"), "%" + request.getName() + "%")
                ));
            }
            if(Objects.nonNull(request.getEmail())){
                predicates.add(builder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }
            if(Objects.nonNull(request.getPhone())){
                predicates.add(builder.like(root.get("phone"), "%" + request.getPhone() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[]{}));
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream()
                .map(this::toContactResponse)
                .toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
    }

}
