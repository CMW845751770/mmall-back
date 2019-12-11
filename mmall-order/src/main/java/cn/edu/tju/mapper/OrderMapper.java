package cn.edu.tju.mapper;

import cn.edu.tju.pojo.Order;
import cn.edu.tju.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    int updateByOrderNoSelective(Order record) ;

    Order selectByOrderNoUserId(@Param("orderNo") Long orderNo ,@Param("userId") Integer userId) ;  ;

    Order selectByOrderNo(@Param("orderNo") Long orderNo) ;

    List<Order> selListByUserId(@Param("userId") Integer userId) ;
}