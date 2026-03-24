package mraffi.learn_sping_restful_api.repository;

import mraffi.learn_sping_restful_api.entity.Address;
import mraffi.learn_sping_restful_api.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

   Optional<Address> findFirstByContactAndId(Contact contact, String id);

}
