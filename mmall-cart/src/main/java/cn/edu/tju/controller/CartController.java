package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.CartService;
import cn.edu.tju.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/cart/")
@Slf4j
public class CartController {

    @Resource
    private RedisTemplate<String,Object> redisTemplate ;
    @Resource
    CartService cartServiceImpl ;

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
    public ServerResponse<CartVo> add(String userKey,Integer productId ,Integer count){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return cartServiceImpl.add(userId,count,productId) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("list.do")
    public ServerResponse<CartVo> list(String userKey) {
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return cartServiceImpl.list(userId) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("update.do")
    public ServerResponse<CartVo> update(String userKey , Integer count, Integer productId){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return cartServiceImpl.update(userId,productId,count) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("delete_product.do")
    public ServerResponse<CartVo> deleteProduct(String userKey , String productIds){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return cartServiceImpl.deleteProduct(userId , productIds) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("select_all.do")
    public ServerResponse<CartVo> selectAll(String userKey) {
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
           return cartServiceImpl.selectOrUnselect(userId,Const.Cart.CHECKED , null ) ; //用户id ，选中还是补选中，商品id
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("un_select_all.do")
    public ServerResponse<CartVo> unSelectAll(String userKey){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return cartServiceImpl.selectOrUnselect(userId,Const.Cart.UN_CHECKED, null ) ; //用户id ，选中还是补选中，商品id
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }



    @RequestMapping("select.do")
    public ServerResponse<CartVo> select(String userKey ,Integer productId){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return cartServiceImpl.selectOrUnselect(userId,Const.Cart.CHECKED, productId ) ; //用户id ，选中还是补选中，商品id
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("un_select.do")
    public ServerResponse<CartVo> unSelect(String userKey,Integer productId){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
            return cartServiceImpl.selectOrUnselect(userId,Const.Cart.UN_CHECKED, productId ) ; //用户id ，选中还是补选中，商品id
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

    @RequestMapping("get_cart_product_count.do")
    public ServerResponse<Integer> getCartProductCount(String userKey){
        Integer userId = isUserLogin(userKey) ;
        if(userId != null && !userId.equals(Const.USER_NOT_ONLINE)){
           return  cartServiceImpl.selectCartProductCount(userId) ;
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
    }

}
