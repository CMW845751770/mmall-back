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

    @RequestMapping("pay.do")
    public ServerResponse pay(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, Long orderNo) {
        return orderService.pay(userKey,orderNo) ;
    }

    @RequestMapping("query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPayStatus(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, Long orderNo) {
        return orderService.queryOrderPayStatus(userKey,orderNo) ;
    }

}
