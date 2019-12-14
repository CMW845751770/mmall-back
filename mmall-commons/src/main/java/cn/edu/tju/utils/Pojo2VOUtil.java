package cn.edu.tju.utils;

import cn.edu.tju.form.ShippingForm;
import cn.edu.tju.pojo.OrderItem;
import cn.edu.tju.pojo.Product;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.pojo.User;
import cn.edu.tju.vo.OrderItemVo;
import cn.edu.tju.vo.ProductDetailVo;
import cn.edu.tju.vo.ShippingVo;
import cn.edu.tju.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.text.DateFormat;

/**
 * @Author: CMW天下第一
 */
@Slf4j
public class Pojo2VOUtil{

    public static UserVO user2userVO(User user){
        UserVO userVO = new UserVO() ;
        BeanUtils.copyProperties(user , userVO);
        return userVO ;
    }

    public static ProductDetailVo product2ProductDetailVo(Product product) {
        String imgPrefix = PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/")  ;
        log.info("【imgPrefix为:】{}",imgPrefix);
        ProductDetailVo productDetailVo = new ProductDetailVo() ;
        BeanUtils.copyProperties(product,productDetailVo);
        if(product.getCreateTime()!=null)
        {
            productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()) );
        }
        log.info("【转换后的create时间字符串为：】",productDetailVo.getCreateTime());
        if(product.getUpdateTime() != null )
        {
            productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        }
        log.info("【转换后的update时间字符串为：】",productDetailVo.getUpdateTime());
        productDetailVo.setImageHost(imgPrefix);
        return productDetailVo  ;
    }

    public static ShippingVo shipping2ShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo() ;
        BeanUtils.copyProperties(shipping,shippingVo) ;
        return shippingVo ;
    }

    public static OrderItemVo orderItem2OrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo() ;
        BeanUtils.copyProperties(orderItem,orderItemVo);
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo ;
    }

    public static Shipping shippingForm2Shipping(ShippingForm shippingForm){
        Shipping shipping = new Shipping() ;
        BeanUtils.copyProperties(shippingForm,shipping);
        return shipping ;
    }
}
