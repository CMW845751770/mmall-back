package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.CartService;
import cn.edu.tju.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/cart/")
@Slf4j
public class CartController {

    @Resource
    CartService cartServiceImpl;

    @RequestMapping("add.do")
    public ServerResponse add(String userKey, Integer productId, Integer count) {
        return cartServiceImpl.add(userKey, productId, count);
    }

    @RequestMapping("list.do")
    public ServerResponse list(String userKey) {
        return cartServiceImpl.list(userKey);
    }

    @RequestMapping("update.do")
    public ServerResponse update(String userKey, Integer count, Integer productId) {
        return cartServiceImpl.update(userKey, count, productId);
    }

    @RequestMapping("delete_product.do")
    public ServerResponse deleteProduct(String userKey, String productIds) {
        return cartServiceImpl.deleteProduct(userKey, productIds);
    }

    @RequestMapping("select_all.do")
    public ServerResponse selectAll(String userKey) {
        return cartServiceImpl.selectOrUnselect(userKey, Const.Cart.CHECKED, null); //用户id ，选中还是补选中，商品id
    }

    @RequestMapping("un_select_all.do")
    public ServerResponse unSelectAll(String userKey) {
        return cartServiceImpl.selectOrUnselect(userKey, Const.Cart.UN_CHECKED, null); //用户id ，选中还是补选中，商品id
    }


    @RequestMapping("select.do")
    public ServerResponse select(String userKey, Integer productId) {
        return cartServiceImpl.selectOrUnselect(userKey, Const.Cart.CHECKED, productId); //用户id ，选中还是补选中，商品id
    }

    @RequestMapping("un_select.do")
    public ServerResponse unSelect(String userKey, Integer productId) {
        return cartServiceImpl.selectOrUnselect(userKey, Const.Cart.UN_CHECKED, productId); //用户id ，选中还是补选中，商品id
    }

    @RequestMapping("get_cart_product_count.do")
    public ServerResponse getCartProductCount(String userKey) {
        return cartServiceImpl.selectCartProductCount(userKey);
    }

}
