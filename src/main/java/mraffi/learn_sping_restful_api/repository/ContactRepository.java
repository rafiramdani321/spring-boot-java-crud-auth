package mraffi.learn_sping_restful_api.repository;

import mraffi.learn_sping_restful_api.entity.Contact;
import mraffi.learn_sping_restful_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {

   Optional<Contact> findFirstByUserAndId(User user, String id);

}
