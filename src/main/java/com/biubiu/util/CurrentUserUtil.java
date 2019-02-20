package com.biubiu.util;

import org.apache.shiro.SecurityUtils;

/**
 * @author 张海彪
 * @create 2019-02-20 11:26
 */
public class CurrentUserUtil {

    /**
     * 获取当前登陆用户名
     *
     * @return 当前登陆用户名
     */
    public static String currentUser() {
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

}
