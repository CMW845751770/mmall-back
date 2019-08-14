package cn.edu.tju.repository.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.pojo.User;
import cn.edu.tju.repository.UserRepository;
import cn.edu.tju.utils.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Resource
    private RedisTemplate<String,Object> redisTemplate ;
    @Resource
    private RedisTemplate<String,String> myStringStringRedisTemplate ;

    @Override
    public boolean deleteUser(String key)
    {
        return redisTemplate.delete(key) ;
    }

    @Override
    public User selectUserByKey(String key) {
        User user = (User) redisTemplate.opsForValue().get(key);
        return user;
    }

    @Override
    public void insertToken(String key, String token) {
        myStringStringRedisTemplate.opsForValue().set(key,token);
        myStringStringRedisTemplate.expire(key, Const.TOKEN_EXPIRES_TIME,TimeUnit.MINUTES) ;
    }

    @Override
    public String getToken(String key) {
        return myStringStringRedisTemplate.opsForValue().get(key) ;
    }
}
