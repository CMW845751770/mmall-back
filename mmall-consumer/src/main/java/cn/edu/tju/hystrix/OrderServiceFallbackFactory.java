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
        } ;
    }
}
