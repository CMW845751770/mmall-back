package cn.edu.tju.hystrix;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.OrderService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
public class OrderServiceFallbackFactory implements FallbackFactory<OrderService> {
    @Override
    public OrderService create(Throwable cause) {
        return  new OrderService() {
            @Override
            public ServerResponse pay(String userKey, Long orderNo) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<Boolean> queryOrderPayStatus(String userKey, Long orderNo) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse create(String userKey, Integer shippingId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse cancel(String userKey, Long orderNo) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse getOrderCartProduct(String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse detail(String userKey, Long orderNo) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse list(String userKey, int pageNum, int pageSize) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }
        } ;
    }
}
