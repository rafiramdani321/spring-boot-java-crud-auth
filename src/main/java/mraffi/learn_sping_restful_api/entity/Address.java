package mraffi.learn_sping_restful_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private String street;
    private String city;
    private String province;
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id", nullable = false)
    @Setter(AccessLevel.NONE)
    private Contact contact;

    public static Address create(String id, Contact contact){
        Address address = new Address();
        address.id = id;
        address.contact = contact;
        return address;
    }
}
