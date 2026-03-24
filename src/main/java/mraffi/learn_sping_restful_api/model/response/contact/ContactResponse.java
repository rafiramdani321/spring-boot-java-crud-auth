package mraffi.learn_sping_restful_api.model.response.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

}
