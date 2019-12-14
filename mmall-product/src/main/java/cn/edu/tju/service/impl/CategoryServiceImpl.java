package cn.edu.tju.service.impl;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.mapper.CategoryMapper;
import cn.edu.tju.pojo.Category;
import cn.edu.tju.service.CategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper ;


    @Override
    @Cacheable(cacheNames = "categoryId_list", unless = "#result.status ne 0")
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = new HashSet<>() ;
        findChildCategory(categorySet, categoryId);
        List<Integer> categoryIdList = new ArrayList<>() ;
        if (categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


    //递归算法,算出子节点
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        //查找子节点,递归算法一定要有一个退出的条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
