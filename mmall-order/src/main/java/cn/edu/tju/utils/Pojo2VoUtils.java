package cn.edu.tju.utils;

import cn.edu.tju.pojo.OrderItem;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.vo.OrderItemVo;
import cn.edu.tju.vo.ShippingVo;
import org.springframework.beans.BeanUtils;

public class Pojo2VoUtils {

    public static ShippingVo shipping2ShippingVo(Shipping shipping){
        if(shipping == null ){
            return null ;
        }
        ShippingVo shippingVo = new ShippingVo() ;
        BeanUtils.copyProperties(shipping,shippingVo) ;
        return shippingVo ;
    }

    public static OrderItemVo orderItem2OrderItemVo(OrderItem orderItem){
        if(orderItem == null ){
            return null ;
        }
        OrderItemVo orderItemVo = new OrderItemVo() ;
        BeanUtils.copyProperties(orderItem,orderItemVo);
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo ;
    }

}
