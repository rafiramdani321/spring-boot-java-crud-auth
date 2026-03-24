package mraffi.learn_sping_restful_api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UpdateAddressRequest {

   @JsonIgnore
   private String id;

   @JsonIgnore
   private String contactId;

   @Size(max = 200)
   private String street;

   @Size(max = 100)
   private String city;

   @Size(max = 100)
   private String province;

   @NotBlank(message = "Country is required")
   @Size(max = 100)
   private String country;

   @Size(max = 10)
   private String postalCode;

}
