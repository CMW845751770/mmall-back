package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.ShippingForm;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: CMW天下第一
 */
@RestController
@Slf4j
public class PayController {

    @Resource
    private PayService payServiceImpl ;

    @RequestMapping("/order/pay.do")
    public ServerResponse pay(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, Long orderNo) {
        return payServiceImpl.pay(userKey,orderNo) ;
    }

    @RequestMapping("/order/query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPayStatus(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, Long orderNo) {
        return payServiceImpl.queryOrderPayStatus(userKey,orderNo) ;
    }

    @RequestMapping("/shipping/add.do")
    public ServerResponse add(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, ShippingForm shipping){
        return payServiceImpl.add(shipping,userKey) ;
    }

    @RequestMapping("/shipping/del.do")
    public ServerResponse del(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey
            ,@RequestParam("shippingId") Integer shippingId){
        return payServiceImpl.del(userKey,shippingId) ;
    }

    @RequestMapping("/shipping/update.do")
    public ServerResponse update(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, @RequestBody ShippingForm shipping){
        return payServiceImpl.update(shipping,userKey) ;
    }

    @RequestMapping("/shipping/select.do")
    public ServerResponse select(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey
            ,@RequestParam("shippingId") Integer shippingId){
        return payServiceImpl.select(userKey,shippingId) ;
    }

    @RequestMapping("/shipping/list.do")
    public ServerResponse list(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey
            , @RequestParam(defaultValue = "1") Integer pageNum
            , @RequestParam(defaultValue = "10")Integer pageSize){
        return payServiceImpl.list(userKey,pageNum,pageSize) ;
    }
}
