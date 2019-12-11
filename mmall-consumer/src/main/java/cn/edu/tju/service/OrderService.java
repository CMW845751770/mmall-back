package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "zuul")
public interface OrderService {

    @RequestMapping("/mmall-pay/pay/pay.do")
    ServerResponse pay(@RequestParam("userKey") String userKey,@RequestParam("orderNo") Long orderNo) ;

    @RequestMapping("/mmall-pay/pay/query_order_pay_status.do")
    ServerResponse<Boolean> queryOrderPayStatus(@RequestParam("userKey") String userKey, @RequestParam("orderNo") Long orderNo) ;

    @RequestMapping("/mmall-order/order/create.do")
    ServerResponse create(@RequestParam("userKey")String userKey,@RequestParam("shippingId") Integer shippingId);

    @RequestMapping("/mmall-order/order/cancel.do")
    ServerResponse cancel(@RequestParam("userKey")String userKey,@RequestParam("orderNo") Long orderNo)  ;

    @RequestMapping("/mmall-order/order/get_order_cart_product.do")
    ServerResponse getOrderCartProduct(@RequestParam("userKey") String userKey) ;

    @RequestMapping("/mmall-order/order/detail.do")
    ServerResponse detail(@RequestParam("userKey") String userKey,@RequestParam("orderNo") Long orderNo) ;

    @RequestMapping("/mmall-order/order/list.do")
    ServerResponse list(@RequestParam("userKey") String userKey, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) ;
}
