package mraffi.learn_sping_restful_api.service;

import lombok.RequiredArgsConstructor;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.exception.ApiException;
import mraffi.learn_sping_restful_api.model.request.LoginRequest;
import mraffi.learn_sping_restful_api.model.response.auth.LoginResponse;
import mraffi.learn_sping_restful_api.repository.UserRepository;
import mraffi.learn_sping_restful_api.security.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public LoginResponse login(LoginRequest request){
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ApiException(
                        "LOGIN_FAILED",
                        HttpStatus.UNAUTHORIZED,
                        "global",
                        "Username or password wrong"
                ));

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return LoginResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        }else{
            throw new ApiException(
                    "LOGIN_FAILED",
                    HttpStatus.UNAUTHORIZED,
                    "global",
                    "Username or password wrong"
            );
        }
    }

    private Long next30Days(){
        return System.currentTimeMillis() * (1000 * 16 * 24 * 30);
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }
}
