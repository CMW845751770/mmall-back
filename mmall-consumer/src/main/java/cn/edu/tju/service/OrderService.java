package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.hystrix.OrderServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "order",fallbackFactory = OrderServiceFallbackFactory.class)
public interface OrderService {

    @RequestMapping("/order/create.do")
    ServerResponse create(@RequestParam("userKey")String userKey,@RequestParam("shippingId") Integer shippingId);

    @RequestMapping("/order/cancel.do")
    ServerResponse cancel(@RequestParam("userKey")String userKey,@RequestParam("orderNo") Long orderNo)  ;

    @RequestMapping("/order/get_order_cart_product.do")
    ServerResponse getOrderCartProduct(@RequestParam("userKey") String userKey) ;

    @RequestMapping("/order/detail.do")
    ServerResponse detail(@RequestParam("userKey") String userKey,@RequestParam("orderNo") Long orderNo) ;

    @RequestMapping("/order/list.do")
    ServerResponse list(@RequestParam("userKey") String userKey, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) ;
}
