package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.vo.CartVo;

public interface CartService {

    ServerResponse<CartVo> add(Integer userId , Integer count, Integer productId) ;

    ServerResponse<CartVo> list(Integer userId) ;

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    ServerResponse<CartVo> selectOrUnselect(Integer userId, Integer checked, Integer productId);

    ServerResponse<Integer> selectCartProductCount(Integer userId);
}
