package com.biubiu.shiro;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 张海彪
 * @create 2019-02-20 18:26
 */
@Slf4j
public class ShiroRedisCache<K, V> implements Cache<K, V> {

    private static final String PREFIX = "SHIRO_SESSION_ID";

    private static final int EXPIRE = 3600;

    private RedisTemplate redisTemplate;

    public ShiroRedisCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(K key) throws CacheException {
        log.info("======get======");
        if (key == null) return null;
        byte[] bytes = getBytesKey(key);
        Object value = redisTemplate.opsForValue().get(bytes);
        if (value == null) return null;
        return ObjectUtil.unserialize((byte[]) value);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        log.info("======put======");
        if (key == null || value == null) return null;
        byte[] bytes = getBytesKey(key);
        redisTemplate.opsForValue().set(bytes, ObjectUtil.serialize(value), EXPIRE, TimeUnit.SECONDS);
        return value;
    }

    @Override
    public V remove(K key) throws CacheException {
        log.info("======remove======");
        if (key == null) return null;
        byte[] bytes = getBytesKey(key);
        Object value = redisTemplate.opsForValue().get(bytes);
        if (value == null) return null;
        redisTemplate.delete(bytes);
        return ObjectUtil.unserialize((byte[]) value);
    }

    @Override
    public void clear() throws CacheException {
        log.info("======clear======");
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public int size() {
        log.info("======size======");
        return redisTemplate.getConnectionFactory().getConnection().dbSize().intValue();
    }

    @Override
    public Set<K> keys() {
        log.info("======keys======");
        byte[] bytes = (PREFIX + "*").getBytes();
        Set<byte[]> keys = redisTemplate.keys(bytes);
        Set<K> sets = new HashSet<>();
        for (byte[] key : keys) {
            sets.add((K) key);
        }
        return sets;
    }

    @Override
    public Collection<V> values() {
        log.info("======values======");
        Set<K> keys = keys();
        List<V> values = new ArrayList<>();
        for (K key : keys) {
            values.add(get(key));
        }
        return values;
    }

    private byte[] getBytesKey(K key) {
        if (key instanceof String) {
            String preKey = PREFIX + key;
            return preKey.getBytes();
        } else {
            return ObjectUtil.serialize(key);
        }
    }

}
