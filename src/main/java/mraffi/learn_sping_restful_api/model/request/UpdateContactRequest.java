package mraffi.learn_sping_restful_api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateContactRequest {

   @JsonIgnore
   private String id;

   @NotBlank(message = "Firstname is required")
   @Size(min = 4, max = 100, message = "Firstname length must be be between 4 and 100")
   private String firstName;

   @Size(max = 100)
   private String lastName;

   @Size(max = 100)
   @Email(message = "Email not valid")
   private String email;

   @Size(max = 100)
//   @Pattern() valitaion dengan regex
   private String phone;

}
