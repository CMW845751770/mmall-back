package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService ;


    @RequestMapping("create.do")
    public ServerResponse create(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, Integer shippingId){
        return orderService.create(userKey,shippingId) ;
    }

    @RequestMapping("cancel.do")
    public ServerResponse cancel(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey ,  Long orderNo){
        return orderService.cancel(userKey,orderNo) ;
    }

    @RequestMapping("get_order_cart_product.do")
    public ServerResponse getOrderCartProduct(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey ){
        return orderService.getOrderCartProduct(userKey) ;
    }

    @RequestMapping("detail.do")
    public ServerResponse detail(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey,Long orderNo){
        return orderService.detail(userKey,orderNo) ;
    }

    @RequestMapping("list.do")
    public ServerResponse list(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        return orderService.list(userKey,pageNum,pageSize) ;
    }

}
