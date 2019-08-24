package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.Shipping;

public interface ShippingService {
    ServerResponse add(Integer userId , Shipping shipping) ;
    ServerResponse del(Integer shippingId,Integer userId);
    ServerResponse update(Integer userId , Shipping shipping) ;
    ServerResponse select(Integer userId, Integer shippingId);
    ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
}
