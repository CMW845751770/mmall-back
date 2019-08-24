package cn.edu.tju.hystrix;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.CartService;
import cn.edu.tju.vo.CartVo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CartServiceFallbackFactory implements FallbackFactory<CartService> {


    @Override
    public CartService create(Throwable cause) {
        return new CartService() {
            @Override
            public ServerResponse<CartVo> add(String userKey, Integer productId, Integer count) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<CartVo> list(String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<CartVo> update(String userKey, Integer count, Integer productId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<CartVo> deleteProduct(String userKey, String productIds) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<CartVo> selectAll(String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<CartVo> unSelectAll(String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<CartVo> select(String userKey, Integer productId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<CartVo> unSelect(String userKey, Integer productId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<Integer> getCartProductCount(String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }
        } ;
    }
}
