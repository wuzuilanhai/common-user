package com.biubiu.mapper;

import com.biubiu.po.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author 张海彪
 * @create 2019-02-19 14:08
 */
@Repository
public interface UserMapper extends Mapper<User> {

    User selectUser(@Param("username") String username);

    int uniqueName(@Param("username") String username);

}
