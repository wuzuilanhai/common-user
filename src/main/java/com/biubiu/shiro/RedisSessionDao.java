package com.biubiu.shiro;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author 张海彪
 * @create 2019-02-20 20:34
 */
@Slf4j
public class RedisSessionDao extends CachingSessionDAO {

    private static final String PREFIX = "SHIRO_SESSION_ID";

    private static final int EXPIRE = 3600;

    private RedisTemplate<byte[], byte[]> redisTemplate;

    public RedisSessionDao(RedisTemplate<byte[], byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doUpdate(Session session) {
        log.info("--------doUpdateSession-----");
        if (session == null) return;
        session.setTimeout(EXPIRE * 1000);
        redisTemplate.opsForValue().set(getByteKey(session.getId()), ObjectUtil.serialize(session), EXPIRE, TimeUnit.SECONDS);
    }

    @Override
    protected void doDelete(Session session) {
        log.info("--------doDeleteSession-----");
        redisTemplate.delete(getByteKey(session.getId()));
    }

    @Override
    protected Serializable doCreate(Session session) {
        log.info("--------doCreateSession-----");
        Serializable serializable = this.generateSessionId(session);
        assignSessionId(session, serializable);
        session.setTimeout(EXPIRE * 1000);
        redisTemplate.opsForValue().set(getByteKey(serializable), ObjectUtil.serialize(session), EXPIRE, TimeUnit.SECONDS);
        return serializable;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        log.info("--------doReadSession-----");
        Session session = null;
        byte[] key = getByteKey(sessionId);
        byte[] value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            session = ObjectUtil.unserialize(value);
            redisTemplate.expire(key, EXPIRE, TimeUnit.MILLISECONDS);
        }
        return session;
    }

    private byte[] getByteKey(Object key) {
        if (key instanceof String) {
            String preKey = PREFIX + key;
            return preKey.getBytes();
        } else {
            return ObjectUtil.serialize(key);
        }
    }

}
