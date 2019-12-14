package cn.edu.tju.hystrix;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.ShippingForm;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.service.PayService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: CMW天下第一
 */
@Component

public class PayServiceFallbackFactory implements FallbackFactory<PayService> {
    @Override
    public PayService create(Throwable cause) {
        return new PayService() {
            @Override
            public ServerResponse pay(String userKey, Long orderNo) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<Boolean> queryOrderPayStatus(String userKey, Long orderNo) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse add(ShippingForm shipping, String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse del(String userKey, Integer shippingId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse update(ShippingForm shipping, String userKey) {
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
