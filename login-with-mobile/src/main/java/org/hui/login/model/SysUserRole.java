package org.hui.login.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 角色和用户对应关系
 *
 * @author zenghui
 * @date 2020-05-20
 */
@Entity
@Table(name = "sys_user_role")
@IdClass(UserRoleKey.class)
public class SysUserRole implements Serializable {
    @Id
    @Column(name = "user_id")
    private Integer userId;
    @Id
    @Column(name = "role_id")
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
