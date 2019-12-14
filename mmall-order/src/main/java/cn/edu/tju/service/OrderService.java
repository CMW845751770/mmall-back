package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.vo.OrderVo;

public interface OrderService {

    ServerResponse create(String userKey , Integer shippingId) ;

    ServerResponse cancel(String userKey, Long orderNo);

    ServerResponse getOrderCartProduct(String userKey);

    ServerResponse detail(String userKey, Long orderNo);

    ServerResponse list(String userKey, int pageNum, int pageSize);

    void updateOrderStatus(Long orderNo) ;
}
