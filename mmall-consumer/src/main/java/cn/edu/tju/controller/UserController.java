package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.UserUpdateForm;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@Slf4j
@RequestMapping("/user/")
@RestController
public class UserController {

    @Resource
    private UserService userService ;

    @PostMapping("check_valid.do")
    public ServerResponse checkValid(String type , String str)
    {
        return userService.checkValid(type,str) ;
    }

    @PostMapping("get_user_info.do")
    public ServerResponse getUserInfo(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey) {
        return userService.getUserInfo(userKey) ;
    }

    @PostMapping("forget_get_question.do")
    public ServerResponse forgetGetQuestion(String username)
    {
        return userService.forgetGetQuestion(username) ;
    }

    @PostMapping("forget_check_answer.do")
    public ServerResponse forgetCheckAnswer(String username , String question , String answer) {
        return userService.forgetCheckAnswer(username,question,answer) ;
    }

    @PostMapping("forget_reset_password.do")
    public ServerResponse forgetRestPassword(String username , String passwordNew , String forgetToken) {
        return userService.forgetRestPassword(username,passwordNew,forgetToken) ;
    }

    @PostMapping("reset_password.do")
    public ServerResponse resetPassword(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey ,String passwordOld,String passwordNew) {
        return userService.resetPassword(passwordOld,passwordNew,userKey) ;
    }

    @PostMapping("update_information.do")
    public ServerResponse update_information(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey , UserUpdateForm user) {
        return userService.update_information(user, userKey);
    }
    @PostMapping("get_information.do")
    public ServerResponse<User> get_information(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey) {
        return userService.get_information(userKey) ;
    }
}
