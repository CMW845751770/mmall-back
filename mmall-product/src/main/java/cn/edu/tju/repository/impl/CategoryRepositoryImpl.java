package cn.edu.tju.repository.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.pojo.Category;
import cn.edu.tju.repository.CategoryRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    @Resource
    private RedisTemplate<String,Object> myStringRedisTemplate ;

    @Override
    public void saveCategorySet(String categoryKey, String categorySetStr) {
        myStringRedisTemplate.opsForValue().set(categoryKey,categorySetStr);
        myStringRedisTemplate.expire(categoryKey, Const.DEFAULT_REDIS_SAVE_TIME, TimeUnit.MINUTES) ; //默认事件为10分钟
    }

    @Override
    public String getCategorySet(String categoryKey) {
        return (String) myStringRedisTemplate.opsForValue().get(categoryKey);
    }
}
