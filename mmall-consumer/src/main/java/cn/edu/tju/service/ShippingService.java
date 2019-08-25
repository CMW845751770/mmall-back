package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.hystrix.ShippingServiceFallbackFactory;
import cn.edu.tju.pojo.Shipping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "zuul",fallbackFactory = ShippingServiceFallbackFactory.class)
public interface ShippingService {

    @RequestMapping(value ="/mmall-pay/shipping/add.do")
    ServerResponse add(@RequestBody Shipping shipping,@RequestParam("userKey") String userKey) ;

    @RequestMapping(value ="/mmall-pay/shipping/del.do")
    ServerResponse del(@RequestParam("userKey")String userKey,@RequestParam("shippingId") Integer shippingId) ;

    @RequestMapping(value ="/mmall-pay/shipping/update.do")
    ServerResponse update(@RequestBody Shipping shipping,@RequestParam("userKey")String userKey) ;

    @RequestMapping(value ="/mmall-pay/shipping/select.do")
    ServerResponse select(@RequestParam("userKey")String userKey ,@RequestParam("shippingId")  Integer shippingId) ;

    @RequestMapping(value ="/mmall-pay/shipping/list.do")
    ServerResponse list(@RequestParam("userKey")String userKey , @RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum
            , @RequestParam(defaultValue = "10",value = "pageSize")Integer pageSize) ;

}
