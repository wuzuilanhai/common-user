package com.biubiu.mapper;

import com.biubiu.po.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 张海彪
 * @create 2019-02-19 14:08
 */
@Repository
public interface RoleMapper extends Mapper<Role> {

    List<Role> findByUid(@Param("userId") String userId);

}
