package cn.edu.tju.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @SuppressWarnings("rawtypes")
    @Bean("myStringRedisTemplate")
    public RedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate stringRedisTemplate = new RedisTemplate();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.setKeySerializer(stringSerializer);
        stringRedisTemplate.setValueSerializer(stringSerializer);
        stringRedisTemplate.setHashKeySerializer(stringSerializer);
        stringRedisTemplate.setHashValueSerializer(stringSerializer);
        return stringRedisTemplate;
    }
}