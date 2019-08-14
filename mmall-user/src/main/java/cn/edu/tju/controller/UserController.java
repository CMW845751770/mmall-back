package cn.edu.tju.controller;


import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.UserService;
import cn.edu.tju.utils.CookieUtil;
import cn.edu.tju.utils.KeyUtil;
import jdk.nashorn.internal.ir.RuntimeNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequestMapping("/user/")
@RestController
public class UserController
{
    @Resource
    private UserService userServiceImpl ;

    @PostMapping("login.do")
    public ServerResponse<User> login(@RequestParam("username") String username ,@RequestParam("password") String password
                               )
    {
        return userServiceImpl.login(username,password) ;
    }

    @PostMapping("logout.do")
    public ServerResponse logout(@RequestParam("userKey") String userKey)
    {
        log.info("cookie value： {}",userKey);
        if(StringUtils.isNotEmpty(userKey))
        userServiceImpl.deleteUser(userKey) ;
        return ServerResponse.createBySuccessMessage("退出成功") ;
    }

    @PostMapping("register.do")
    public ServerResponse register(@RequestBody User user)
    {
        return userServiceImpl.register(user)  ;
    }

    @PostMapping("check_valid.do")
    public ServerResponse checkValid(String type , String str)
    {
        return userServiceImpl.checkValid(str,type) ;
    }

    @PostMapping("get_user_info.do")
    public ServerResponse getUserInfo(@RequestParam("userKey") String userKey)
    {
        log.info("get_user_info.do  cookie value{}",userKey);
        if(StringUtils.isNotBlank(userKey))
        {
            //去redis中取用户信息
            User user = userServiceImpl.getUserInfo(userKey);
            if(user!= null)
            {
                return ServerResponse.createBySuccess(user) ;
            }
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息") ;
    }

    @PostMapping("forget_get_question.do")
    public ServerResponse forgetGetQuestion(String username)
    {
        return userServiceImpl.getUserQuestion(username) ;
    }

    @PostMapping("forget_check_answer.do")
    public ServerResponse forgetCheckAnswer(String username , String question , String answer)
    {
        return userServiceImpl.checkAnswer(username,question,answer) ;
    }

    @PostMapping("forget_reset_password.do")
    public ServerResponse forgetRestPassword(String username , String passwordNew , String forgetToken)
    {
        return userServiceImpl.forgetResetPassword(username,passwordNew,forgetToken) ;
    }

    @PostMapping("reset_password.do")
    public ServerResponse resetPassword(@RequestParam("passwordOld") String passwordOld,@RequestParam("passwordNew") String passwordNew,@RequestParam("userKey")String userKey)
    {
        if(StringUtils.isNotBlank(userKey))
        {
            User user = userServiceImpl.getUserInfo(userKey);
            if(user!=null)
            {
                return userServiceImpl.resetPassword(passwordOld,passwordNew,user) ;
            }
        }
        return ServerResponse.createByErrorMessage("用户未登录" );
    }

    @PostMapping("update_information.do")
    public ServerResponse<User> update_information(@RequestParam("userKey")String userKey ,@RequestBody User user)
    {
        if(StringUtils.isNotBlank(userKey))
        {
            User currentUser = userServiceImpl.getUserInfo(userKey);
            if(currentUser!=null)
            {
                //进行更新用户信息操作
                user.setId(currentUser.getId());
                user.setUsername(currentUser.getUsername());
                return userServiceImpl.updateUserInformation(user) ;
            }
        }
        return ServerResponse.createByErrorMessage("用户未登录" );
    }

    @PostMapping("get_information.do")
    public ServerResponse<User> get_information(@RequestParam("userKey")String userKey){
        if(StringUtils.isNotBlank(userKey))
        {
            User currentUser = userServiceImpl.getUserInfo(userKey);
            if(currentUser!=null)
            {
                //获取用户信息
                return userServiceImpl.getInformation(currentUser.getId()) ;
            }
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
    }
}
