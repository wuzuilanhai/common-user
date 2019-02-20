package com.biubiu.controller;

import com.biubiu.exception.CustomException;
import com.biubiu.mapper.UserMapper;
import com.biubiu.po.User;
import com.biubiu.util.CurrentUserUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 张海彪
 * @create 2019-02-19 15:24
 */
@RestController
@RequestMapping("/rest")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/user")
    @RequiresAuthentication
    public Object user(@RequestBody User user) {
        String currentUser = CurrentUserUtil.currentUser();
        String username = user.getUsername();
        if (userMapper.uniqueName(username) > 0) {
            throw new CustomException("用户名已存在");
        }
        String password = user.getPassword();
        ByteSource salt = ByteSource.Util.bytes(username);
        //注意，这里加密参数的类型要完全吻合才行，salt不能取字符串，只能取ByteSource类型来传输
        String cryptPwd = new Sha256Hash(password.toCharArray(), salt, 2).toString();
        User newUser = User.builder()
                .username(username)
                .password(cryptPwd)
                .salt(salt.toBase64())
                .creator(currentUser)
                .editor(currentUser)
                .build();
        return userMapper.insertSelective(newUser);
    }

    @PostMapping("/login")
    public Object login(@RequestBody User user) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        token.setRememberMe(true);
        subject.login(token);
        return "login success";
    }

    @GetMapping("/logout")
    public Object logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        return "logout success";
    }

    @GetMapping("/test/role")
    @RequiresRoles("role0")
    public void testRole() {

    }

    @GetMapping("/test/permission")
    @RequiresPermissions("permission0")
    public void testPermission() {

    }

}
