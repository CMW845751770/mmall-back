package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.PageImpl;

public interface ProductService {

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId) ;

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy) ;

    ServerResponse decreaseStock(String orderItemListStr) ;
}
