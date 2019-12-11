package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.UserForm;
import cn.edu.tju.form.UserUpdateForm;
import cn.edu.tju.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    ServerResponse login(String username , String password, HttpServletResponse response) ;

    ServerResponse logout(HttpServletRequest request,HttpServletResponse response) ;

    ServerResponse register(UserForm user) ;

    ServerResponse checkValid(String str , String type) ;

    ServerResponse getUserInfo(String key) ;

    ServerResponse getUserQuestion(String username) ;

    ServerResponse checkAnswer(String username , String question , String answer) ;

    ServerResponse forgetResetPassword(String username , String passwordNew ,String forgetToken) ;

    ServerResponse resetPassword(String passwordOld, String passwordNew,String userKey) ;

    ServerResponse updateUserInformation(UserUpdateForm user, String userKey) ;

    ServerResponse getInformation(String userKey);
}
