package cn.edu.tju.service.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.mapper.UserMapper;
import cn.edu.tju.pojo.User;
import cn.edu.tju.repository.UserRepository;
import cn.edu.tju.service.UserService;
import cn.edu.tju.utils.CookieUtil;
import cn.edu.tju.utils.KeyUtil;
import cn.edu.tju.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper ;
    @Resource
    private UserRepository userRepositoryImpl ;
    @Override
    public ServerResponse<User> login(String username, String password)
    {
        //先判断用户是否存在
        int resultCount = userMapper.selectByUsername(username) ;
        if(resultCount < 1 )
        {
            return ServerResponse.createByErrorMessage("用户名不存在") ;
        }
        //TODO 密码经过md5加密
        String mad5Password = MD5Util.MD5EncodeUtf8(password,username) ;
        User user = userMapper.selectByUsernameAndPassword(username,mad5Password) ;
        if(user == null)
        {
           return ServerResponse.createByErrorMessage("密码错误") ;
        }
        user.setPassword(null);
        return ServerResponse.createBySuccess("登陆成功",user) ;
    }

    @Override
    public ServerResponse<User> register(User user) {
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
    public boolean deleteUser(String key) {
        return userRepositoryImpl.deleteUser(key);
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

    @Override
    public User getUserInfo(String key) {
        return userRepositoryImpl.selectUserByKey(key);
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
            userRepositoryImpl.insertToken(Const.TOKEN_PREFIX+username,token);
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
        String userToken = userRepositoryImpl.getToken(Const.TOKEN_PREFIX+username) ;
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
    public ServerResponse resetPassword(String passwordOld, String passwordNew,User user) {
        String md5PasswordOld = MD5Util.MD5EncodeUtf8(passwordOld,user.getUsername()) ;
        String resultPasswordOld = userMapper.selectPasswordByUserId(user.getId()) ;
        if(!resultPasswordOld.equals(md5PasswordOld))
        {
            return ServerResponse.createByErrorMessage("旧密码错误") ;
        }
        //更新密码
        int rows = userMapper.updatePasswordByUserId(user.getId(),MD5Util.MD5EncodeUtf8(passwordNew,user.getUsername())) ;
        if(rows > 0 )
        {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse updateUserInformation(User user) {
        //判断email是否存在
        int resultCount = userMapper.selectCountByUserIdAndEmail(user.getId(),user.getEmail()) ;
        if(resultCount > 0 )
        {
            return ServerResponse.createByErrorMessage("email已存在") ;
        }
        //进行更新操作email,phone,question,answer
        User updateUser = new User() ;
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int rows = userMapper.updateByPrimaryKeySelective(updateUser) ;
        if(rows > 0 )
        {
            updateUser.setUsername(user.getUsername());
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败") ;
    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id) ;
        if(user != null )
        {
            user.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccess(user) ;
        }
        return ServerResponse.createByErrorMessage("找不到当前用户");
    }
}
