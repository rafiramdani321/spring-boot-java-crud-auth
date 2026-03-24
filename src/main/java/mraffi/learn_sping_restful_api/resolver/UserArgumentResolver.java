package mraffi.learn_sping_restful_api.resolver;

import jakarta.servlet.http.HttpServletRequest;
import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.exception.ApiException;
import mraffi.learn_sping_restful_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = servletRequest.getHeader("X-API-TOKEN");
        if(token == null){
            throw new ApiException(
                    "UNAUTHORIZED",
                    HttpStatus.UNAUTHORIZED,
                    "global",
                    "Unauthorized"
            );
        }

        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ApiException(
                        "UNAUTHORIZED",
                        HttpStatus.UNAUTHORIZED,
                        "global",
                        "Unauthorized"
                ));

        if(user.getTokenExpiredAt() < System.currentTimeMillis()){
            throw new ApiException(
                    "TOKEN_EXPIRED",
                    HttpStatus.UNAUTHORIZED,
                    "global",
                    "Unauthorized"
            );
        }

        return user;
    }
}
