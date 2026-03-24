package mraffi.learn_sping_restful_api.helper;

import mraffi.learn_sping_restful_api.entity.Contact;
import mraffi.learn_sping_restful_api.entity.User;

import java.util.UUID;

public class ContactTestFactory {

    public static Contact createContact(User user){
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("rafi");
        contact.setLastName("ramdani");
        contact.setEmail("example@gmail.com");
        contact.setPhone("081274574845");
        contact.setUser(user);
        return contact;
    }

}
