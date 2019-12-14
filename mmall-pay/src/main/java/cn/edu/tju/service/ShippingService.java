package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.ShippingForm;
import cn.edu.tju.pojo.Shipping;

public interface ShippingService {
    ServerResponse add(String userKey , ShippingForm shippingForm) ;
    ServerResponse del(String userKey, Integer shippingId);
    ServerResponse update(String userKey , ShippingForm shippingForm);
    ServerResponse select(String userKey, Integer shippingId);
    ServerResponse list(String userKey, Integer pageNum, Integer pageSize);
}
