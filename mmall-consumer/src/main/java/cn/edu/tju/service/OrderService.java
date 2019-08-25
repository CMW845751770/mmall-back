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

}
