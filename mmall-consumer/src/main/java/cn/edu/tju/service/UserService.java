package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.UserUpdateForm;
import cn.edu.tju.hystrix.UserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user",fallbackFactory = UserServiceFallbackFactory.class)
public interface UserService {

    @PostMapping("/user/check_valid.do")
    ServerResponse checkValid(@RequestParam("type") String type ,@RequestParam("str") String str);

    @PostMapping("/user/get_user_info.do")
    ServerResponse getUserInfo(@RequestParam("userKey") String userKey) ;

    @PostMapping("/user/forget_get_question.do")
    ServerResponse forgetGetQuestion(@RequestParam("username") String username);

    @PostMapping("/user/forget_check_answer.do")
    ServerResponse forgetCheckAnswer(@RequestParam("username") String username , @RequestParam("question") String question , @RequestParam("answer")String answer) ;

    @PostMapping("/user/forget_reset_password.do")
    ServerResponse forgetRestPassword(@RequestParam("username")String username , @RequestParam("passwordNew")String passwordNew , @RequestParam("forgetToken")String forgetToken) ;

    @PostMapping("/user/reset_password.do")
    ServerResponse resetPassword(@RequestParam("passwordOld") String passwordOld,@RequestParam("passwordNew")String passwordNew,@RequestParam("userKey")String userKey) ;

    @PostMapping("/user/update_information.do")
    ServerResponse update_information(@RequestBody UserUpdateForm user, @RequestParam("userKey") String userKey)  ;

    @PostMapping("/user/get_information.do")
    ServerResponse get_information(@RequestParam("userKey")String userKey) ;
}
