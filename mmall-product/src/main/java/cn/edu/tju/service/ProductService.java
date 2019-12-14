package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.OrderItem;
import cn.edu.tju.vo.OrderItemVo;
import cn.edu.tju.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface ProductService {

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId) ;

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy) ;

    void decreaseStock(List<OrderItemVo> orderItemVoList) ;
}
