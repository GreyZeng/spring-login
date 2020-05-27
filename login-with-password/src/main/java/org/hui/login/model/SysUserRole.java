package org.hui.login.model;

/**
 * 角色和用户对应关系
 * @author zenghui
 * @date 2020-05-20
 */
public class SysUserRole  {
    private Integer userId;
    private Integer roleId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
