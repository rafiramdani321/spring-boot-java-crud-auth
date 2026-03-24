package mraffi.learn_sping_restful_api.service;

import lombok.RequiredArgsConstructor;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.exception.ApiException;
import mraffi.learn_sping_restful_api.model.request.RegisterRequest;
import mraffi.learn_sping_restful_api.model.request.UpdateUserRequest;
import mraffi.learn_sping_restful_api.model.response.user.RegisterResponse;
import mraffi.learn_sping_restful_api.model.response.user.UserResponse;
import mraffi.learn_sping_restful_api.repository.UserRepository;
import mraffi.learn_sping_restful_api.security.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public RegisterResponse register(RegisterRequest request){

        if(userRepository.existsById(request.getUsername())){
            throw new ApiException(
                    "USER_ALREADY_EXIST",
                    HttpStatus.BAD_REQUEST,
                    "username",
                    "Username already registered"
            );
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);

        return RegisterResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse get(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request){
        if(Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }

        if(Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }
}
