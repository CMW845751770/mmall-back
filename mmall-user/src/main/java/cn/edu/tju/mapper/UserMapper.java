package cn.edu.tju.mapper;

import cn.edu.tju.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int selectByUsername(@Param("username") String username) ;

    int selectByEmail(@Param("email") String email) ;

    User selectByUsernameAndPassword(@Param("username") String username , @Param("password") String password) ;

    String selectQuestionByUsername(@Param("username") String username) ;

    int selectCountByUsernameAndQuestionAndAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer) ;

    int updatePasswordByUsername(@Param("passwordNew") String passwordNew ,@Param("username") String username) ;

    String selectPasswordByUserId(@Param("id") Integer id) ;

    int updatePasswordByUserId(@Param("id")Integer id ,@Param("passwordNew") String passworNew) ;

    int selectCountByUserIdAndEmail(@Param("id") Integer id ,@Param("email") String email) ;
}