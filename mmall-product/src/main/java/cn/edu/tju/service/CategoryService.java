package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;

import java.util.List;

public interface CategoryService {

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) ;
}
