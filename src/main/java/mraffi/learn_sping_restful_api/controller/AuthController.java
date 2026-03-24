package mraffi.learn_sping_restful_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.model.request.LoginRequest;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.auth.LoginResponse;
import mraffi.learn_sping_restful_api.model.response.user.UserResponse;
import mraffi.learn_sping_restful_api.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse loginResponse = authService.login(request);
        return WebResponse.<LoginResponse>builder()
                .data(loginResponse)
                .message("Login Success")
                .build();
    }

    // argumentResolver User user bisa diubah menjadi annotations @currentUser
    @DeleteMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(User user){
        authService.logout(user);
        return WebResponse.<String>builder()
                .message("Logout Success")
                .build();
    }
}
