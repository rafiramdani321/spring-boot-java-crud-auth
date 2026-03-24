package mraffi.learn_sping_restful_api.helper;

import mraffi.learn_sping_restful_api.entity.User;
import mraffi.learn_sping_restful_api.security.BCrypt;

public class UserTestFactory {

    public static User createUser(){
        User user = new User();
        user.setUsername("test-username");
        user.setName("test-name");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        return user;
    }

    public static User createUserWithToken(){
        User user = createUser();
        user.setToken("token");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        return user;
    }

    public static User createExpiredUser(){
        User user = createUser();
        user.setToken("token");
        user.setTokenExpiredAt(System.currentTimeMillis() - 100000000);
        return user;
    }

}
