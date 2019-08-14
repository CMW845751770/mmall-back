package cn.edu.tju.repository;

import cn.edu.tju.pojo.User;

public interface UserRepository {

    boolean deleteUser(String key) ;

    User selectUserByKey(String key) ;

    void insertToken(String key , String token) ;

    String getToken(String key) ;
}
