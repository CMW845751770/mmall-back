package cn.edu.tju.hystrix;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.ProductService;
import cn.edu.tju.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceFallBackFactory implements FallbackFactory<ProductService> {
    @Override
    public ProductService create(Throwable cause) {
        return new ProductService() {
            @Override
            public ServerResponse<ProductDetailVo> detail(Integer productId) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<PageInfo> list(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }
        } ;
    }
}
