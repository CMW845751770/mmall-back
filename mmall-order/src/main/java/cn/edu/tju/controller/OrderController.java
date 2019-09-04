package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OrderService orderServiceImpl ;
    private Integer isUserLogin(String userKey) {
        if (StringUtils.isNotBlank(userKey)) {
            User user = (User) redisTemplate.opsForValue().get(userKey);
            if (user != null) {
                return user.getId();
            }
        }
        return Const.USER_NOT_ONLINE;
    }

    @RequestMapping("create.do")
    public ServerResponse create(String userKey, Integer shippingId){
        Integer userId = isUserLogin(userKey) ;
            if(userId != null && userId != Const.USER_NOT_ONLINE){
                //service
                return orderServiceImpl.create(userId,shippingId) ;
            }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                                            , ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("cancel.do")
    public ServerResponse cancel(String userKey, Long orderNo){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && userId != Const.USER_NOT_ONLINE){
            //service
            return orderServiceImpl.cancel(userId,orderNo) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                , ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("get_order_cart_product.do")
    public ServerResponse getOrderCartProduct(String userKey){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && userId != Const.USER_NOT_ONLINE){
            //service
            return orderServiceImpl.getOrderCartProduct(userId) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                , ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("detail.do")
    public ServerResponse detail(String userKey,Long orderNo){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && userId != Const.USER_NOT_ONLINE){
            //service
            return orderServiceImpl.detail(userId,orderNo) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                , ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("list.do")
    public ServerResponse list(String userKey, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && userId != Const.USER_NOT_ONLINE){
            //service
            return orderServiceImpl.list(userId,pageNum,pageSize);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                , ResponseCode.NEED_LOGIN.getDesc()) ;
    }
}
