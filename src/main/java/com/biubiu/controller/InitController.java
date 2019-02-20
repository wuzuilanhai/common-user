package com.biubiu.controller;

import com.biubiu.exception.CustomException;
import com.biubiu.mapper.*;
import com.biubiu.po.*;
import com.biubiu.util.CurrentUserUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 张海彪
 * @create 2019-02-20 15:10
 */
@RestController
@RequestMapping("/rest")
public class InitController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @GetMapping("/role/init")
    @RequiresAuthentication
    @Transactional
    public void initRole() {
        String currentUser = CurrentUserUtil.currentUser();
        List<User> users = userMapper.selectAll();
        for (int i = 0; i < users.size(); i++) {
            Role role = Role.builder().name("role" + i).description("role" + i)
                    .creator(currentUser).editor(currentUser).build();
            roleMapper.insertSelective(role);
            String roleId = role.getId();
            if (StringUtils.isEmpty(roleId)) throw new CustomException("添加角色失败");
            UserRole userRole = UserRole.builder().userId(users.get(i).getId())
                    .roleId(roleId).creator(currentUser).editor(currentUser).build();
            userRoleMapper.insertSelective(userRole);
        }
    }

    @GetMapping("/permission/init")
    @RequiresAuthentication
    @Transactional
    public void initPermission() {
        String currentUser = CurrentUserUtil.currentUser();
        List<Role> roles = roleMapper.selectAll();
        for (int i = 0; i < roles.size(); i++) {
            Permission permission = Permission.builder().permission("permission" + i).description("permission" + i)
                    .creator(currentUser).editor(currentUser).build();
            permissionMapper.insertSelective(permission);
            String permissionId = permission.getId();
            if (StringUtils.isEmpty(permissionId)) throw new CustomException("添加权限失败");
            RolePermission rolePermission = RolePermission.builder().roleId(roles.get(i).getId())
                    .permissionId(permissionId).creator(currentUser).editor(currentUser).build();
            rolePermissionMapper.insertSelective(rolePermission);
        }
    }

}
