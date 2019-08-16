package cn.edu.tju.service.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.mapper.CategoryMapper;
import cn.edu.tju.pojo.Category;
import cn.edu.tju.repository.CategoryRepository;
import cn.edu.tju.service.CategoryService;
import cn.edu.tju.utils.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
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

    @Resource
    private CategoryRepository categoryRepositoryImpl ;

    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        //先从redis中找
        String categoryKey = Const.REDIS_CATEGORY_KEY_PREFIX + categoryId ;
        String categorySetStr = categoryRepositoryImpl.getCategorySet(categoryKey);
        Set<Category> categorySet = null ;
        if(StringUtils.isBlank(categorySetStr )) //redis中没有
        {
            categorySet = new HashSet<>() ;
            //从数据库中寻找
            findChildCategory(categorySet, categoryId);
            //存入redis中
            categoryRepositoryImpl.saveCategorySet(categoryKey, JacksonUtil.bean2Json(categorySet));
        }else {
            categorySet =  JacksonUtil.json2BeanT(categorySetStr, new TypeReference<Set<Category>>() {}) ;
        }
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
