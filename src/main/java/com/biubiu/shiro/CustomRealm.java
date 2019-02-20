package com.biubiu.shiro;

import com.biubiu.exception.CustomException;
import com.biubiu.mapper.PermissionMapper;
import com.biubiu.mapper.RoleMapper;
import com.biubiu.mapper.UserMapper;
import com.biubiu.po.Permission;
import com.biubiu.po.Role;
import com.biubiu.po.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 张海彪
 * @create 2019-02-19 17:19
 */
public class CustomRealm extends AuthorizingRealm {

    private static final String CURRENT_USER = "currentUser";
    private static final String CURRENT_USER_ID = "currentUserId";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        if (username == null) {
            throw new CustomException("用户名不能为空");
        }
        User user = userMapper.selectUser(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }
        String password = user.getPassword();
        ByteSource credentialsSalt = ByteSource.Util.bytes(username);
        AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(token.getPrincipal(), password, credentialsSalt, getName());
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(CURRENT_USER, user);
        session.setAttribute(CURRENT_USER_ID, user.getId());
        return authenticationInfo;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userId = (String) SecurityUtils.getSubject().getSession().getAttribute(CURRENT_USER_ID);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //获取用户角色
        List<Role> roleList = roleMapper.findByUid(userId);
        if (!roleList.isEmpty()) {
            info.setRoles(roleList.stream().map(Role::getName).collect(Collectors.toSet()));
            //获取角色所拥有的权限
            List<Permission> permissionList = permissionMapper.findByRids(roleList.stream().map(Role::getId).collect(Collectors.toList()));
            if (!permissionList.isEmpty()) {
                info.setStringPermissions(permissionList.stream().map(Permission::getPermission).collect(Collectors.toSet()));
            }
        }
        return info;
    }

}
