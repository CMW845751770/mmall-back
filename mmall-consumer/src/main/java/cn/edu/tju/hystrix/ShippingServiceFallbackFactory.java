package cn.edu.tju.hystrix;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.service.ShippingService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ShippingServiceFallbackFactory implements FallbackFactory<ShippingService> {

    @Override
    public ShippingService create(Throwable cause) {
        return new ShippingService() {
            @Override
            public ServerResponse add( Shipping shipping,String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse del(String userKey, Integer shippingId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse update( Shipping shipping,String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse select(String userKey, Integer shippingId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse list(String userKey, Integer pageNum, Integer pageSize) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }
        };
    }
}
