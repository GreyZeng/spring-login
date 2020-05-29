package org.hui.login.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 用户角色
 *
 * @author zenghui
 * @date 2020-05-20
 */
@Entity
@Table(name = "sys_role")
public class SysRole implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
