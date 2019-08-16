package cn.edu.tju.controller;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.ProductService;
import cn.edu.tju.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Resource
    private ProductService productServiceImpl ;

    @RequestMapping("detail.do")
    public ServerResponse<ProductDetailVo> detail(@RequestParam("productId")Integer productId)
    {
        return productServiceImpl.getProductDetail(productId) ;
    }

    @RequestMapping("list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return productServiceImpl.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy) ;
    }
}
