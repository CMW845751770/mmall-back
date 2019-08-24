package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.ShippingService;
import org.apache.catalina.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.applet.Main;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RequestMapping("/shipping/")
@RestController
public class ShippingController {

    @Resource
    private RedisTemplate<String,Object> redisTemplate ;
    @Resource
    private ShippingService shippingServiceImpl ;

    private Integer isUserLogin(String userKey){
        if(StringUtils.isNotBlank(userKey)){
            User user = (User) redisTemplate.opsForValue().get(userKey);
            if(user != null ){
                return user.getId() ;
            }
        }
        return Const.USER_NOT_ONLINE;
    }

    @RequestMapping("add.do")
    public ServerResponse add(String userKey, Shipping shipping){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return shippingServiceImpl.add(userId,shipping) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("del.do")
    public ServerResponse del(String userKey, Integer shippingId){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return shippingServiceImpl.del(shippingId,userId) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("update.do")
    public ServerResponse update(String userKey , Shipping shipping){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return shippingServiceImpl.update(userId,shipping) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("select.do")
    public ServerResponse select(String userKey , Integer shippingId){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return  shippingServiceImpl.select(userId , shippingId);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("list.do")
    public ServerResponse list(String userKey , @RequestParam(defaultValue = "1") Integer pageNum
                                , @RequestParam(defaultValue = "10")Integer pageSize){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return  shippingServiceImpl.list(userId , pageNum, pageSize) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

}
