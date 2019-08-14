package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.UserService;
import cn.edu.tju.utils.CookieUtil;
import cn.edu.tju.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequestMapping("/user/")
@RestController
public class UserController {

    @Resource
    private UserService userService ;

    @Resource
    private RedisTemplate<String,Object> redisTemplate ;

    @PostMapping("login.do")
    public ServerResponse<User> login(String username,String password,HttpServletResponse response)
    {
         ServerResponse<User> serverResponse = userService.login(username,password);
         if(serverResponse.isSuccess())
         {
             String userKey = KeyUtil.genUniqueKey() ;
             //往redis中插入用户信息
             User user = serverResponse.getData();
             redisTemplate.opsForValue().set(userKey , user);
             redisTemplate.expire(userKey, CookieUtil.COOKIE_HALF_HOUR, TimeUnit.SECONDS) ;
             //设置cookie
             CookieUtil.setCookie(response, Const.CURRENT_USER,userKey,CookieUtil.COOKIE_HALF_HOUR);
             log.info("用户{} 于{}登陆成功 生成的token为{}",username ,LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),userKey);
         }
         return serverResponse ;
    }

    @PostMapping("logout.do")
    public ServerResponse logout(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, HttpServletRequest request, HttpServletResponse response)
    {
        //清除cookie
        CookieUtil.removeCookie(request,response,Const.CURRENT_USER);
        return userService.logout(userKey) ;
    }

    @PostMapping("register.do")
    public ServerResponse register(User user)
    {
        return userService.register(user)  ;
    }

    @PostMapping("check_valid.do")
    public ServerResponse checkValid(String type , String str)
    {
        return userService.checkValid(type,str) ;
    }

    @PostMapping("get_user_info.do")
    public ServerResponse getUserInfo(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey)
    {
        return userService.getUserInfo(userKey) ;
    }

    @PostMapping("forget_get_question.do")
    public ServerResponse forgetGetQuestion(String username)
    {
        return userService.forgetGetQuestion(username) ;
    }

    @PostMapping("forget_check_answer.do")
    public ServerResponse forgetCheckAnswer(String username , String question , String answer)
    {
        return userService.forgetCheckAnswer(username,question,answer) ;
    }

    @PostMapping("forget_reset_password.do")
    public ServerResponse forgetRestPassword(String username , String passwordNew , String forgetToken)
    {
        return userService.forgetRestPassword(username,passwordNew,forgetToken) ;
    }

    @PostMapping("reset_password.do")
    public ServerResponse resetPassword(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey ,String passwordOld,String passwordNew)
    {
        return userService.resetPassword(passwordOld,passwordNew,userKey) ;
    }

    @PostMapping("update_information.do")
    public ServerResponse update_information(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey , User user)
    {
        ServerResponse<User> serverResponse=  userService.update_information(user,userKey) ;
        if(serverResponse.isSuccess())
        {
            //往redis中插入用户信息
            User userNew = serverResponse.getData();
            redisTemplate.opsForValue().set(userKey , userNew);
            redisTemplate.expire(userKey, CookieUtil.COOKIE_HALF_HOUR, TimeUnit.SECONDS) ;
        }
        return serverResponse ;
    }

    @PostMapping("get_information.do")
    public ServerResponse<User> get_information(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey) {
        return userService.get_information(userKey) ;
    }
}
