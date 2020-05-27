package org.hui.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.hui.login.model.SysUser;
/**
 * @author zenghui
 * @date 2020-05-20
 */
@Mapper
public interface SysUserMapper {
    @Select("SELECT * FROM sys_user WHERE name = #{name}")
    SysUser selectByName(String name);
}