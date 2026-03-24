package mraffi.learn_sping_restful_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.model.request.RegisterRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateUserRequest;
import mraffi.learn_sping_restful_api.model.response.WebResponse;
import mraffi.learn_sping_restful_api.model.response.user.RegisterResponse;
import mraffi.learn_sping_restful_api.model.response.user.UserResponse;
import mraffi.learn_sping_restful_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping(
            path = "/api/users/register",
            consumes = MediaType.APPLICATION_JSON_VALUE, // req
            produces = MediaType.APPLICATION_JSON_VALUE // res
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request){
        RegisterResponse registerResponse = userService.register(request);
        return WebResponse.<RegisterResponse>builder()
                .data(registerResponse)
                .message("Register Success")
                .build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user){
        UserResponse userResponse = userService.get(user);
        return WebResponse.<UserResponse>builder()
                .data(userResponse)
                .message("Fetching Get User Success")
                .build();
    }

    @PatchMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @Valid @RequestBody UpdateUserRequest request){
        UserResponse userResponse = userService.update(user, request);
        return WebResponse.<UserResponse>builder()
                .data(userResponse)
                .message("Update Success")
                .build();
    }
}
