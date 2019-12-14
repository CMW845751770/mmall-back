package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.hystrix.ProductServiceFallBackFactory;
import cn.edu.tju.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "product" ,fallbackFactory = ProductServiceFallBackFactory.class)
public interface ProductService {

    @RequestMapping(value = "/product/detail.do")
    ServerResponse<ProductDetailVo> detail(@RequestParam("productId")Integer productId)  ;

    @RequestMapping(value = "/product/list.do")
    ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy) ;
}
