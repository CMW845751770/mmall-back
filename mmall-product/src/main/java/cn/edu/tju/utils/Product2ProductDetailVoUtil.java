package cn.edu.tju.utils;

import cn.edu.tju.pojo.Product;
import cn.edu.tju.vo.ProductDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import java.text.DateFormat;

@Slf4j
public class Product2ProductDetailVoUtil {

    public static synchronized  ProductDetailVo  product2ProductDetailVo(Product product)
    {
        String imgPrefix = PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/")  ;
        log.info("【imgPrefix为:】{}",imgPrefix);
        ProductDetailVo productDetailVo = new ProductDetailVo() ;
        BeanUtils.copyProperties(product,productDetailVo);
        DateFormat dateFormat = DateFormat.getDateTimeInstance() ;
        if(product.getCreateTime()!=null)
        {
            productDetailVo.setCreateTime(dateFormat.format(product.getCreateTime()));
        }
        log.info("【转换后的create时间字符串为：】",productDetailVo.getCreateTime());
        if(product.getUpdateTime() != null )
        {
            productDetailVo.setUpdateTime(dateFormat.format(product.getUpdateTime()));
        }
        log.info("【转换后的update时间字符串为：】",productDetailVo.getUpdateTime());
        productDetailVo.setImageHost(imgPrefix);
        return productDetailVo  ;
    }

}
