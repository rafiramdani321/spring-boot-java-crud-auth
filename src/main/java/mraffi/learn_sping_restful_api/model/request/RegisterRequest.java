package mraffi.learn_sping_restful_api.model.request;

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
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 100)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 100, message = "Password length must be between 4 and 100")
    private String password;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;
}
