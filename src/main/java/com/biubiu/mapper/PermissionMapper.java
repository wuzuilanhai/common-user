package com.biubiu.mapper;

import com.biubiu.po.Permission;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 张海彪
 * @create 2019-02-19 14:09
 */
@Repository
public interface PermissionMapper extends Mapper<Permission> {

    List<Permission> findByRids(List<String> rids);

}
