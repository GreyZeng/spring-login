package org.hui.login.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author zenghui
 * @date 2020-5-29
 */
@Entity
@Table(name = "sys_permission")
public class SysPermission implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "url")
    private String url;
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "permission")
    private String permission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
