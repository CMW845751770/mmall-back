package cn.edu.tju.service.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.UserForm;
import cn.edu.tju.form.UserUpdateForm;
import cn.edu.tju.mapper.UserMapper;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.UserService;
import cn.edu.tju.utils.*;
import cn.edu.tju.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper ;

    @Resource
    private StringRedisTemplate stringRedisTemplate ;

    @Override
    public ServerResponse login(String username, String password, HttpServletResponse response)
    {
        //先判断用户是否存在
        int resultCount = userMapper.selectByUsername(username) ;
        if(resultCount < 1 )
        {
            return ServerResponse.createByErrorMessage("用户名不存在") ;
        }
        String mad5Password = MD5Util.MD5EncodeUtf8(password,username) ;
        User user = userMapper.selectByUsernameAndPassword(username,mad5Password) ;
        if(user == null)
        {
           return ServerResponse.createByErrorMessage("用户名与密码不匹配") ;
        }
        //登陆成功
        UserVO userVO = Pojo2VOUtil.user2userVO(user) ;
        //设置use对象
        setUserInSession(userVO,response) ;
        return ServerResponse.createBySuccess("登陆成功",userVO) ;
    }

    private void setUserInSession(UserVO userVO,HttpServletResponse response){
        String userKey = KeyUtil.genUniqueKey() ;
        //往redis中插入用户信息
        stringRedisTemplate.opsForValue().set(userKey , JacksonUtil.bean2Json(userVO));
        stringRedisTemplate.expire(userKey, CookieUtil.COOKIE_HALF_HOUR, TimeUnit.SECONDS) ;
        //设置cookie
        CookieUtil.setCookie(response, Const.CURRENT_USER,userKey,CookieUtil.COOKIE_HALF_HOUR);
        log.info("用户{} 于{}登陆成功 生成的token为{}",userVO.getUsername() , LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),userKey);
    }

    @Override
    public ServerResponse logout(HttpServletRequest request,HttpServletResponse response) {
        String token = CookieUtil.getCookieValue(request,Const.CURRENT_USER) ;
        if(!StringUtils.isBlank(token)){
            stringRedisTemplate.delete(token) ;
            CookieUtil.removeCookie(request,response,Const.CURRENT_USER) ;
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse register(UserForm userForm) {
        if(userForm == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        User user = Form2PojoUtil.userForm2User(userForm) ;
        //判断用户是否存在
        int usernameCount = userMapper.selectByUsername(user.getUsername()) ;
        if(usernameCount > 0 )
        {
            return ServerResponse.createByErrorMessage("用户已存在") ;
        }
        //判断email是否存在
        int emailCount = userMapper.selectByEmail(user.getEmail()) ;
        if(emailCount> 0 )
        {
            return ServerResponse.createByErrorMessage("email已存在") ;
        }
        //密码加密存储
        String md5Password = MD5Util.MD5EncodeUtf8(user.getPassword(),user.getUsername()) ;
        user.setPassword(md5Password);
        //默认为普通用户
        user.setRole(Const.ROLE.ROLE_CUSTOMER);
        //插入用户
        int resultCount = userMapper.insert(user) ;
        if(resultCount <1 )
        {
            return ServerResponse.createByErrorMessage("注册失败") ;
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse checkValid(String str, String type) {
        log.info("str={}  type={}",str,type);
        if(StringUtils.isNotBlank(type))
        {
            if(Const.USERNAME.equals(type))
            {
                int usernameCount = userMapper.selectByUsername(str) ;
                if(usernameCount > 0 )
                {
                    return ServerResponse.createByErrorMessage("用户名已存在") ;
                }
            }else if(Const.EMAIL.equals(type))
            {
                int emailCount = userMapper.selectByEmail(str) ;
                if(emailCount > 0 )
                {
                    return ServerResponse.createByErrorMessage("email已存在" );
                }
            }else {
                return ServerResponse.createByErrorMessage("参数错误") ;
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误") ;
        }
        return ServerResponse.createBySuccessMessage("校验成功" );
    }

    private UserVO getUserVOInSession(String token){
        if(StringUtils.isBlank(token)){
            return null ;
        }
        String userJson = stringRedisTemplate.opsForValue().get(token) ;
        if(StringUtils.isBlank(userJson)){
            return null ;
        }
        UserVO userVO = JacksonUtil.json2Bean(userJson , UserVO.class) ;
        return userVO ;
    }

    @Override
    public ServerResponse getUserInfo(String key) {
        UserVO userVO = getUserVOInSession(key) ;
        if(userVO != null ){
            return ServerResponse.createBySuccess(userVO) ;
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息") ;
    }

    @Override
    public ServerResponse getUserQuestion(String username) {
        int resultCount = userMapper.selectByUsername(username) ;
        if(resultCount < 1)
        {
            return ServerResponse.createByErrorMessage("该用户不存在") ;
        }
        String question = userMapper.selectQuestionByUsername(username) ;
        if(question!=null)
        {
            return ServerResponse.createBySuccess(question) ;
        }
        return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
    }

    @Override
    public ServerResponse checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.selectCountByUsernameAndQuestionAndAnswer(username,question,answer) ;
        if(resultCount > 0 )//问题与答案匹配
        {
            //生成一个token，存储在redis中
            String token = KeyUtil.genUniqueKey() ;
            stringRedisTemplate.opsForValue().set(Const.TOKEN_PREFIX+username,token);
            return ServerResponse.createBySuccess(token) ;
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken))
        {
            return ServerResponse.createByErrorMessage("参数错误,token需要传递")  ;
        }
        int resultCount = userMapper.selectByUsername(username) ;
        if(resultCount < 1 )
        {
            return ServerResponse.createByErrorMessage("用户不存在") ;
        }
        String userToken = stringRedisTemplate.opsForValue().get(Const.TOKEN_PREFIX+username) ;
        if(StringUtils.isBlank(userToken))
        {
            return ServerResponse.createByErrorMessage("token无效或者过期") ;
        }
        if(!forgetToken.equals(userToken))
        {
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token") ;
        }
        //重置密码
        String md5PasswordNew = MD5Util.MD5EncodeUtf8(passwordNew,username) ;
        int rows = userMapper.updatePasswordByUsername(md5PasswordNew,username) ;
        if(rows > 0 )
        {
            return ServerResponse.createByErrorMessage("修改密码成功") ;
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse resetPassword(String passwordOld, String passwordNew,String userKey) {
        UserVO userVO = getUserVOInSession(userKey);
        if(userVO == null ){
            return ServerResponse.createByErrorMessage("用户未登录" );
        }
        String md5PasswordOld = MD5Util.MD5EncodeUtf8(passwordOld,userVO.getUsername()) ;
        String resultPasswordOld = userMapper.selectPasswordByUserId(userVO.getId()) ;
        if(!resultPasswordOld.equals(md5PasswordOld))
        {
            return ServerResponse.createByErrorMessage("旧密码错误") ;
        }
        //更新密码
        int rows = userMapper.updatePasswordByUserId(userVO.getId(),MD5Util.MD5EncodeUtf8(passwordNew,userVO.getUsername())) ;
        if(rows > 0 )
        {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }


    @Override
    public ServerResponse updateUserInformation(UserUpdateForm userUpdateForm, String userKey) {
        UserVO userVO = getUserVOInSession(userKey);
        if(userVO == null ){
            return ServerResponse.createByErrorMessage("用户未登录" );
        }
        if(userUpdateForm == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        User user = Form2PojoUtil.userUpdateForm2User(userUpdateForm) ;
        //判断email是否存在
        int resultCount = userMapper.selectCountByUserIdAndEmail(userVO.getId(),user.getEmail()) ;
        if(resultCount > 0 )
        {
            return ServerResponse.createByErrorMessage("email已存在") ;
        }
        //进行更新操作email,phone,question,answer
        User updateUser = new User() ;
        updateUser.setId(userVO.getId())
                  .setEmail(user.getEmail())
                  .setPhone(user.getPhone())
                  .setQuestion(user.getQuestion())
                  .setAnswer(user.getAnswer());
        int rows = userMapper.updateByPrimaryKeySelective(updateUser) ;
        if(rows > 0 )
        {
            updateUser.setUsername(userVO.getUsername()) ;
            //更新redis中的用户数据
            stringRedisTemplate.opsForValue().getAndSet(userKey,JacksonUtil.bean2Json(updateUser)) ;
            return ServerResponse.createBySuccess("更新个人信息成功",Pojo2VOUtil.user2userVO(updateUser));
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败") ;
    }

    @Override
    public ServerResponse getInformation(String userKey) {
        UserVO userVO = getUserVOInSession(userKey);
        if(userVO == null ){
            return ServerResponse.createByErrorMessage("用户未登录" );
        }
        User user = userMapper.selectByPrimaryKey(userVO.getId()) ;
        if(user != null )
        {
            return ServerResponse.createBySuccess(Pojo2VOUtil.user2userVO(user)) ;
        }
        return ServerResponse.createByErrorMessage("找不到当前用户");
    }
}
