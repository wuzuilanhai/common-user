package com.biubiu.shiro;

import cn.hutool.core.codec.Base64;
import com.google.common.collect.Maps;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author 张海彪
 * @create 2019-02-19 14:59
 */
@Configuration
public class ShiroConfig {

    private static final String ALGORITHM_NAME = "SHA-256";

    private static final String CIPHER_KEY = "3AvVhmFLUs0KTA3Kprsdag==";

    private static final String SESSION_ID_NAME = "biubiu.session.id";

    //session 超时时间，默认1800000毫秒
    private static final int SESSION_TIMEOUT = 3600000;

    //rememberMe cookie有效时长，默认30天
    private static final int COOKIE_TIMEOUT = 2592000;

    //advisorAutoProxyCreator()和authorizationAttributeSourceAdvisor()是为了让注解@RequiresRoles和@RequiresPermissions起作用
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    //开启aop注解
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 使用md5 算法进行加密
        hashedCredentialsMatcher.setHashAlgorithmName(ALGORITHM_NAME);
        // 设置散列次数： 意为加密几次
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }

    @Bean
    public CustomRealm myRealm() {
        CustomRealm customRealm = new CustomRealm();
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return customRealm;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME);
        // 是否只在https情况下传输
        cookie.setSecure(false);
        // 设置 cookie 的过期时间，单位为秒，这里为一天
        cookie.setMaxAge(COOKIE_TIMEOUT);
        return cookie;
    }

    @Bean
    public SecurityManager securityManager(RedisTemplate redisTemplate) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRememberMeManager(rememberMeManager());
        //缓存管理
        securityManager.setCacheManager(cacheManager(redisTemplate));
        securityManager.setRealm(myRealm());
        //会话管理
        securityManager.setSessionManager(sessionManager(redisTemplate));
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
        shiroFilterFactoryBean.setLoginUrl("/notLogin");
        // 设置无权限时跳转的 url
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        // 设置拦截器
        shiroFilterFactoryBean.setFilterChainDefinitionMap(initMap());
        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionDAO sessionDAO(RedisTemplate redisTemplate) {
        return new RedisSessionDao(redisTemplate);
    }

    private Map<String, String> initMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        //游客，开发权限
        map.put("/rest/guest/**", "anon");
        //开放登陆接口
        map.put("/rest/login", "anon");

        //静态资源
        map.put("/css/**", "anon");
        map.put("/files/**", "anon");
        map.put("/images/**", "anon");
        map.put("/img/**", "anon");
        map.put("/js/**", "anon");
        map.put("/scripts/**", "anon");

        //其余接口一律拦截
        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
        map.put("/rest/**", "authc");
        return map;
    }

    private RememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie 加密的密钥
        cookieRememberMeManager.setCipherKey(Base64.decode(CIPHER_KEY));
        return cookieRememberMeManager;
    }

    private CacheManager cacheManager(RedisTemplate redisTemplate) {
        return new ShiroRedisCacheManager(redisTemplate);
    }

    private DefaultWebSessionManager sessionManager(RedisTemplate redisTemplate) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 设置session超时时间，单位为毫秒
        sessionManager.setGlobalSessionTimeout(SESSION_TIMEOUT);
        sessionManager.setSessionIdCookie(new SimpleCookie(SESSION_ID_NAME));
        sessionManager.setSessionDAO(sessionDAO(redisTemplate));
        return sessionManager;
    }

}
