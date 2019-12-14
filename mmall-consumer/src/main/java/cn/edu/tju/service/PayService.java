package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.ShippingForm;
import cn.edu.tju.hystrix.PayServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: CMW天下第一
 */
@FeignClient(value = "pay",fallbackFactory = PayServiceFallbackFactory.class)
public interface PayService {

    @RequestMapping("/pay/pay.do")
    ServerResponse pay(@RequestParam("userKey") String userKey, @RequestParam("orderNo") Long orderNo) ;

    @RequestMapping("/pay/query_order_pay_status.do")
    ServerResponse<Boolean> queryOrderPayStatus(@RequestParam("userKey") String userKey, @RequestParam("orderNo") Long orderNo) ;

    @RequestMapping(value ="/shipping/add.do")
    ServerResponse add(@RequestBody ShippingForm shipping, @RequestParam("userKey") String userKey) ;

    @RequestMapping(value ="/shipping/del.do")
    ServerResponse del(@RequestParam("userKey")String userKey,@RequestParam("shippingId") Integer shippingId) ;

    @RequestMapping(value ="/shipping/update.do")
    ServerResponse update(@RequestBody ShippingForm shipping, @RequestParam("userKey")String userKey) ;

    @RequestMapping(value ="/shipping/select.do")
    ServerResponse select(@RequestParam("userKey")String userKey ,@RequestParam("shippingId")  Integer shippingId) ;

    @RequestMapping(value ="/shipping/list.do")
    ServerResponse list(@RequestParam("userKey")String userKey , @RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum
            , @RequestParam(defaultValue = "10",value = "pageSize")Integer pageSize) ;
}
