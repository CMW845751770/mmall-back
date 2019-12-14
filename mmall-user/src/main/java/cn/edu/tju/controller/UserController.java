package cn.edu.tju.controller;


import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.UserForm;
import cn.edu.tju.form.UserUpdateForm;
import cn.edu.tju.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RequestMapping("/user/")
@RestController
public class UserController {
    @Resource
    private UserService userServiceImpl;

    @PostMapping("login.do")
    public ServerResponse login(@RequestParam("username") String username
            , @RequestParam("password") String password
            , HttpServletResponse response) {
        return userServiceImpl.login(username, password, response);
    }

    @PostMapping("logout.do")
    public ServerResponse logout(HttpServletRequest request, HttpServletResponse response0) {
        return userServiceImpl.logout(request, response0);
    }

    @PostMapping("register.do")
    public ServerResponse register(@Valid UserForm user,BindingResult result) {
        if(result.hasErrors()){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    result.getFieldError().getDefaultMessage()) ;
        }
        return userServiceImpl.register(user);
    }

    @PostMapping("check_valid.do")
    public ServerResponse checkValid(String type, String str) {
        return userServiceImpl.checkValid(str, type);
    }

    @PostMapping("get_user_info.do")
    public ServerResponse getUserInfo(@RequestParam("userKey") String userKey) {
        return userServiceImpl.getUserInfo(userKey);
    }

    @PostMapping("forget_get_question.do")
    public ServerResponse forgetGetQuestion(String username) {
        return userServiceImpl.getUserQuestion(username);
    }

    @PostMapping("forget_check_answer.do")
    public ServerResponse forgetCheckAnswer(String username, String question, String answer) {
        return userServiceImpl.checkAnswer(username, question, answer);
    }

    @PostMapping("forget_reset_password.do")
    public ServerResponse forgetRestPassword(String username, String passwordNew, String forgetToken) {
        return userServiceImpl.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @PostMapping("reset_password.do")
    public ServerResponse resetPassword(@RequestParam("passwordOld") String passwordOld, @RequestParam("passwordNew") String passwordNew, @RequestParam("userKey") String userKey) {
        return userServiceImpl.resetPassword(passwordOld, passwordNew, userKey);
    }

    @PostMapping("update_information.do")
    public ServerResponse update_information(@RequestParam("userKey") String userKey, @Valid @RequestBody UserUpdateForm user, BindingResult result) {
        log.info("user :{}",user);
        if(result.hasErrors()){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    result.getFieldError().getDefaultMessage()) ;
        }
        return userServiceImpl.updateUserInformation(user,userKey);
    }

    @PostMapping("get_information.do")
    public ServerResponse get_information(@RequestParam("userKey") String userKey) {
        return userServiceImpl.getInformation(userKey);
    }
}
