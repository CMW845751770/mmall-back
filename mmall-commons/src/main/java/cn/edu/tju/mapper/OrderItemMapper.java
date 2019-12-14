package cn.edu.tju.mapper;

import cn.edu.tju.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    int insertBatch(@Param("orderItemList") List<OrderItem> orderItemList) ;

    List<OrderItem> selOrderItemListByOrderNoUserId(@Param("orderNo") Long orderNo,@Param("userId") Integer userId) ;

    List<OrderItem> getByOrderNo(@Param("orderNo") Long orderNo);
}