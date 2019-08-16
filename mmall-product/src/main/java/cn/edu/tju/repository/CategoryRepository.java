package cn.edu.tju.repository;

import cn.edu.tju.pojo.Category;

import java.util.Set;

public interface CategoryRepository {

    void saveCategorySet(String categoryKey, String categorySetStr) ;

    String getCategorySet(String categoryKey) ;
}
