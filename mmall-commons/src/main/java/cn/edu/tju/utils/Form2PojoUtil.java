package cn.edu.tju.utils;

import cn.edu.tju.form.UserForm;
import cn.edu.tju.form.UserUpdateForm;
import cn.edu.tju.pojo.User;
import org.springframework.beans.BeanUtils;

/**
 * @Author: CMW天下第一
 */
public class Form2PojoUtil {

    public static User userForm2User(UserForm userForm){
        User user = new User() ;
        BeanUtils.copyProperties(userForm,user);
        return user ;
    }

    public static User userUpdateForm2User(UserUpdateForm userUpdateForm){
        User user = new User()  ;
        BeanUtils.copyProperties(userUpdateForm,user);
        return user ;
    }
}
