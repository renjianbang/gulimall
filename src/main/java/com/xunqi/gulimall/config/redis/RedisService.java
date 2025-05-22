package com.xunqi.gulimall.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //存缓存
    public void set(String key, String value, Long timeOut) {
        redisTemplate.opsForValue().set(key, value, timeOut, TimeUnit.SECONDS);
    }
    //存缓存
    public void setObject(String key, Object value, Long timeOut) {
        redisTemplate.opsForValue().set(key, value, timeOut, TimeUnit.SECONDS);
    }
    //存缓存不设置时间
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    //取缓存
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    //清除缓存
    public void del(String key) {
        redisTemplate.delete(key);
    }

    public Set keys(String pattern){
        return redisTemplate.keys(pattern);
    };

}