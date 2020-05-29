package org.hui.login.model;

import java.io.Serializable;

/**
 * @author zenghui
 * @date 2020-05-29
 */
public class UserRoleKey implements Serializable {
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
