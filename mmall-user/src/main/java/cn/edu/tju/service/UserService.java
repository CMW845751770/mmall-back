package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;

public interface UserService {
    ServerResponse<User> login(String username , String password) ;

    ServerResponse<User> register(User user) ;

    boolean deleteUser(String key) ;

    ServerResponse checkValid(String str , String type) ;

    User getUserInfo(String key) ;

    ServerResponse getUserQuestion(String username) ;

    ServerResponse checkAnswer(String username , String question , String answer) ;

    ServerResponse forgetResetPassword(String username , String passwordNew ,String forgetToken) ;

    ServerResponse resetPassword(String passwordOld, String passwordNew,User user) ;

    ServerResponse updateUserInformation(User user) ;

    ServerResponse<User> getInformation(Integer id);
}
