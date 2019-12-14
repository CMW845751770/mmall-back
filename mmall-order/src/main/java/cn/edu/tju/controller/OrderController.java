package cn.edu.tju.controller;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderServiceImpl;


    @RequestMapping("create.do")
    public ServerResponse create(String userKey, Integer shippingId) {
        return orderServiceImpl.create(userKey, shippingId);
    }

    @RequestMapping("cancel.do")
    public ServerResponse cancel(String userKey, Long orderNo) {
        return orderServiceImpl.cancel(userKey, orderNo);
    }

    @RequestMapping("get_order_cart_product.do")
    public ServerResponse getOrderCartProduct(String userKey) {
        return orderServiceImpl.getOrderCartProduct(userKey);
    }

    @RequestMapping("detail.do")
    public ServerResponse detail(String userKey, Long orderNo) {
        return orderServiceImpl.detail(userKey, orderNo);
    }

    @RequestMapping("list.do")
    public ServerResponse list(String userKey, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return orderServiceImpl.list(userKey, pageNum, pageSize);
    }
}
