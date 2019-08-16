package cn.edu.tju.utils;

import cn.edu.tju.pojo.Product;
import cn.edu.tju.vo.ProductListVo;
import org.springframework.beans.BeanUtils;

public class Product2ProductListVoUtil {

    public static ProductListVo product2ProductListVo(Product product){
        String imgPrefix = PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/")  ;
        ProductListVo productListVo = new ProductListVo() ;
        BeanUtils.copyProperties(product,productListVo) ;
        productListVo.setImageHost(imgPrefix);
        return productListVo ;
    }
}
