package cn.edu.tju.service.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.exception.InadequateStockException;
import cn.edu.tju.mapper.CategoryMapper;
import cn.edu.tju.mapper.ProductMapper;
import cn.edu.tju.message.producer.ModifyOrderStatusProducer;
import cn.edu.tju.pojo.Category;
import cn.edu.tju.pojo.OrderItem;
import cn.edu.tju.pojo.Product;
import cn.edu.tju.service.CategoryService;
import cn.edu.tju.service.ProductService;
import cn.edu.tju.utils.JacksonUtil;
import cn.edu.tju.utils.Pojo2VOUtil;
import cn.edu.tju.utils.Product2ProductListVoUtil;
import cn.edu.tju.vo.OrderItemVo;
import cn.edu.tju.vo.ProductListVo;
import cn.edu.tju.vo.ProductDetailVo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryService categoryServiceImpl;

    @Resource
    private ModifyOrderStatusProducer modifyOrderStatusProducer ;

    @Override
    @Cacheable(cacheNames = "product_detail", unless = "#result.status ne 0")
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    , ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //转换成ProductVo对象
        ProductDetailVo productDetailVo = Pojo2VOUtil.product2ProductDetailVo(product);
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        productDetailVo.setParentCategoryId(category.getParentId());
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    @Cacheable(cacheNames = "product_list", unless = "#result.status ne 0")
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        //如果keyword和categoryId同时为空
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = null;
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            log.info("【category】：{}",category);
            if (category == null && StringUtils.isBlank(keyword)) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> list = new ArrayList<>();
                PageInfo pi = new PageInfo(list);
                return ServerResponse.createBySuccess(pi);
            }
            //递归查询出所有子分类
            categoryIdList = categoryServiceImpl.selectCategoryAndChildrenById(categoryId).getData();
        }
        //分页
        PageHelper.startPage(pageNum, pageSize);
        // 排序
        if (StringUtils.isNotBlank(orderBy)) {
            String[] orderArray = orderBy.split("_");
            if (orderArray.length == 2) //刚好被分成两部分
            {
                PageHelper.orderBy(orderArray[0] + " " + orderArray[1]);
            }
        }
        log.info("keyword:{}    categoryIdLIst: {}",keyword,categoryIdList);
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword
                        , CollectionUtils.isEmpty(categoryIdList)?null:categoryIdList);
        List<ProductListVo> productListVoList = new ArrayList<>();
        //转换成VO对象
        productList.forEach(product -> {
            productListVoList.add(Product2ProductListVoUtil.product2ProductListVo(product));
        });
        PageInfo pi = new PageInfo(productListVoList);
        return ServerResponse.createBySuccess(pi);
    }

    // 异步扣库存
    @Override
    public void decreaseStock(List<OrderItemVo> orderItemVoList) {
        if(CollectionUtils.isEmpty(orderItemVoList)){//要扣的库存没东西？
            log.error("异步扣库存：库存为空");
            return ;
        }
        Long orderNo = orderItemVoList.get(0).getOrderNo() ;
        for (OrderItemVo orderItemVo : orderItemVoList) {
            Product product = productMapper.selectByPrimaryKey(orderItemVo.getProductId());
            if (product != null) {
                if(product.getStock() < orderItemVo.getQuantity()){
                    log.error("异步扣库存：库存不足");
                    throw new InadequateStockException() ;
                }
                product.setStock(product.getStock() - orderItemVo.getQuantity());
            }
        }
        //异步回调修改订单状态
        log.info("开始异步回调修改订单状态{}",orderNo);
        modifyOrderStatusProducer.sendMessage(String.valueOf(orderNo));
        log.info("异步扣库存完成{}",orderItemVoList);
    }
}
