package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.vo.OrderVo;

public interface OrderService {

    ServerResponse<OrderVo> create(Integer userId , Integer shippingId) ;

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse detail(Integer userId, Long orderNo);

    ServerResponse list(Integer userId, int pageNum, int pageSize);
}
