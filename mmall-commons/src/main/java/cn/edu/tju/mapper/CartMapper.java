package cn.edu.tju.mapper;

import cn.edu.tju.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId) ;

    List<Cart> selCartListByUserId(@Param("userId") Integer userId) ;

    List<Cart> selCheckedCartListByUserId(@Param("userId") Integer userId) ;

    int selUnCheckedCountByUserId(@Param("userId") Integer userId) ;

    void deleteByProductIdList(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    int updateByUserIdAndProductId(@Param("userId") Integer userId, @Param("checked") Integer checked, @Param("productId") Integer productId);

    Integer selectCarProductCount(@Param("userId") Integer userId);


}