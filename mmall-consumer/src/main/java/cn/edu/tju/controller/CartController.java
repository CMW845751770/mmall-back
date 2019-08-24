package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.CartService;
import cn.edu.tju.vo.CartVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RequestMapping("/cart/")
@RestController
public class CartController {

    @Resource
    private CartService cartService ;

    @RequestMapping("add.do")
    public ServerResponse<CartVo> add(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, Integer productId , Integer count){
        return cartService.add(userKey,productId,count) ;
    }

    @RequestMapping("list.do")
    public ServerResponse<CartVo> list(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey) {
        return cartService.list(userKey) ;
    }

    @RequestMapping("update.do")
    public ServerResponse<CartVo> update(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey , Integer count, Integer productId){
        return cartService.update(userKey,count,productId) ;
    }

    @RequestMapping("delete_product.do")
    public ServerResponse<CartVo> deleteProduct(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, String productIds){
        return cartService.deleteProduct(userKey,productIds) ;
    }

    @RequestMapping("select_all.do")
    public ServerResponse<CartVo> selectAll(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey) {
        return cartService.selectAll(userKey) ;
    }

    @RequestMapping("un_select_all.do")
    public ServerResponse<CartVo> unSelectAll(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey){
        return cartService.unSelectAll(userKey) ;
    }



    @RequestMapping("select.do")
    public ServerResponse<CartVo> select(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey , Integer productId){
        return cartService.select(userKey,productId) ;
    }

    @RequestMapping("un_select.do")
    public ServerResponse<CartVo> unSelect(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey,Integer productId){
        return cartService.unSelect(userKey,productId) ;
    }

    @RequestMapping("get_cart_product_count.do")
    public ServerResponse<Integer> getCartProductCount(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey){
        return cartService.getCartProductCount(userKey) ;
    }
}
