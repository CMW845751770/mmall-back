package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.vo.CartVo;

public interface CartService {

    ServerResponse add(String userKey,Integer productId ,Integer count) ;

    ServerResponse list(String userKey) ;

    ServerResponse update(String userKey, Integer count, Integer productId);

    ServerResponse deleteProduct(String userKey, String productIds);

    ServerResponse selectOrUnselect(String userKey, Integer checked, Integer productId);

    ServerResponse  selectCartProductCount(String userKey);
}
