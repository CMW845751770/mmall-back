package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.hystrix.UserServiceFallbackFactory;
import cn.edu.tju.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "zuul",fallbackFactory = UserServiceFallbackFactory.class)
public interface UserService {

    @PostMapping(value = "/mmall-user/user/login.do")
    ServerResponse<User> login(@RequestParam("username") String username ,@RequestParam("password") String password
            ) ;

    @PostMapping("/mmall-user/user/logout.do")
    ServerResponse logout(@RequestParam("userKey") String userKey) ;

    @PostMapping("/mmall-user/user/register.do")
    ServerResponse register(@RequestBody()User user);

    @PostMapping("/mmall-user/user/check_valid.do")
    ServerResponse checkValid(@RequestParam("type") String type ,@RequestParam("str") String str);

    @PostMapping("/mmall-user/user/get_user_info.do")
    ServerResponse getUserInfo(@RequestParam("userKey") String userKey) ;

    @PostMapping("/mmall-user/user/forget_get_question.do")
    ServerResponse forgetGetQuestion(@RequestParam("username") String username);

    @PostMapping("/mmall-user/user/forget_check_answer.do")
    ServerResponse forgetCheckAnswer(@RequestParam("username") String username , @RequestParam("question") String question , @RequestParam("answer")String answer) ;

    @PostMapping("/mmall-user/user/forget_reset_password.do")
    ServerResponse forgetRestPassword(@RequestParam("username")String username , @RequestParam("passwordNew")String passwordNew , @RequestParam("forgetToken")String forgetToken) ;

    @PostMapping("/mmall-user/user/reset_password.do")
    ServerResponse resetPassword(@RequestParam("passwordOld") String passwordOld,@RequestParam("passwordNew")String passwordNew,@RequestParam("userKey")String userKey) ;

    @PostMapping("/mmall-user/user/update_information.do")
    ServerResponse<User> update_information( @RequestBody User user,@RequestParam("userKey") String userKey)  ;

    @PostMapping("/mmall-user/user/get_information.do")
    ServerResponse<User> get_information(@RequestParam("userKey")String userKey) ;
}
