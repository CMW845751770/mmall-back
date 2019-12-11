package cn.edu.tju.utils;

import cn.edu.tju.pojo.User;
import cn.edu.tju.vo.UserVO;
import org.springframework.beans.BeanUtils;

/**
 * @Author: CMW天下第一
 */
public class Pojo2VOUtil{

    public static UserVO user2userVO(User user){
        UserVO userVO = new UserVO() ;
        BeanUtils.copyProperties(user , userVO);
        return userVO ;
    }
}
