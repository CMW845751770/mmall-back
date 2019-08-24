package cn.edu.tju.mapper;

import cn.edu.tju.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int deletByUserIdPrimaryKey(@Param("userId") Integer userId ,@Param("id") Integer id) ;

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int updateByExample(Shipping shipping);

    Shipping selectByUserIdPrimaryKey(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);

    List<Shipping> selectListByUserId(@Param("userId") Integer userId);
}