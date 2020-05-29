# Spring Security实现短信验证码登录

## 环境

- JDK 1.8
- Spring Boot 2.3.0.RELEASE
- Maven 3.6.1
- H2数据库

> 注：本次使用JPA作为持久化工具

## 数据结构

/resources/db/schema.sql
```sql
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_permission`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_user_role`;


CREATE TABLE `sys_role` (
                            `id` INT(11) NOT NULL,
                            `name` VARCHAR(255) NOT NULL, PRIMARY KEY (`id`)
) ;

CREATE TABLE `sys_permission` (
                                  `id` INT(11) NOT NULL,
                                  `url` VARCHAR(255) NULL DEFAULT NULL,
                                  `role_id` INT(11) NULL DEFAULT NULL,
                                  `permission` VARCHAR(255) NULL DEFAULT NULL, PRIMARY KEY (`id`), CONSTRAINT `sys_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON
        DELETE CASCADE ON
        UPDATE CASCADE
) ;

CREATE TABLE `sys_user` (
                            `id` INT(11) NOT NULL,
                            `name` VARCHAR(255) NOT NULL,
                            `mobile` VARCHAR(255) NULL DEFAULT NULL,
                            `password` VARCHAR(255) NOT NULL, PRIMARY KEY (`id`)
) ;

CREATE TABLE `sys_user_role` (
                                 `user_id` INT(11) NOT NULL,
                                 `role_id` INT(11) NOT NULL, PRIMARY KEY (`user_id`, `role_id`), CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON
        DELETE CASCADE ON
        UPDATE CASCADE, CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON
        DELETE CASCADE ON
        UPDATE CASCADE


) ;
```

/resources/db/data.sql
```sql
INSERT INTO `sys_role`
VALUES (1, 'ROLE_ADMIN');
INSERT INTO `sys_role`
VALUES (2, 'ROLE_USER');

INSERT INTO `sys_permission`
VALUES (1, '/admin', 1, 'c,r,u,d');
INSERT INTO `sys_permission`
VALUES (2, '/admin', 2, 'r');


INSERT INTO `sys_user`
VALUES (1, 'admin', '110', '2020');
INSERT INTO `sys_user`
VALUES (2, 'user', '119', '2021');

INSERT INTO `sys_user_role`
VALUES (1, 1);
INSERT INTO `sys_user_role`
VALUES (2, 2);
```
## POM依赖
```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.0.RELEASE</version>
        <relativePath/>
    </parent>
    <groupId>org.hui</groupId>
    <artifactId>login-with-mobile</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>login-with-mobile</name>
    <description>Spring Security to implements Login</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

```

## Model设计

SysPermission
```java
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

```


SysUser.java
```java
package org.hui.login.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * 用户基本信息
 *
 * @author zenghui
 * @date 2020-05-20
 */
@Entity
@Table(name = "sys_user")
public class SysUser implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(name = "mobile")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysUser sysUser = (SysUser) o;
        return Objects.equals(id, sysUser.id) &&
                Objects.equals(name, sysUser.name) &&
                Objects.equals(password, sysUser.password) &&
                Objects.equals(mobile, sysUser.mobile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, mobile);
    }
}

```

SysRole.java
```java
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

```

SysUserRole.java
包括了联合主键UserRoleKey.java

```java
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

```
UserRoleKey.java
```java
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

```
为以上建好Repository

SysPermissionRepository.java
```java
package org.hui.login.repository;

import org.hui.login.model.SysPermission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zenghui
 * @author 2020-05-29
 */
@Repository
public interface SysPermissionRepository extends CrudRepository<SysPermission, Integer> {
}
```
SysRoleRepository.java
```java
package org.hui.login.repository;

import org.hui.login.model.SysRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zenghui
 * @author 2020-05-29
 */
@Repository
public interface SysRoleRepository extends CrudRepository<SysRole, Integer> {
}

```

SysUserRepository.java
```java
package org.hui.login.repository;

import org.hui.login.model.SysUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zenghui
 * @author 2020-05-29
 */
@Repository
public interface SysUserRepository extends CrudRepository<SysUser, Integer> {
}
```

SysUserRoleRepository.java
```java
package org.hui.login.repository;

import org.hui.login.model.SysUserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zenghui
 * @author 2020-05-29
 */
@Repository
public interface SysUserRoleRepository extends CrudRepository<SysUserRole, Integer> {
}

```
补充一个Repository的单元测试，验证基本操作没问题
在test中创建一个单元测试类
SysUserRepositoryTest.java

```java
package org.hui.login.repository;

import org.hui.login.model.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SysUserRepositoryTest {
    @Autowired
    private SysUserRepository repository;

    @Test
    public void testQuery() {
        SysUser sysUser = new SysUser();
        sysUser.setId(3);
        sysUser.setMobile("1198");
        sysUser.setName("super");
        sysUser.setPassword("7778");
        repository.save(sysUser);
        Optional<SysUser> optionalSysUser = repository.findById(sysUser.getId());
        Assert.notNull(optionalSysUser.get().equals(sysUser), "add and query user error");


        repository.deleteById(sysUser.getId());
        Optional<SysUser> afterDeleteUser = repository.findById(sysUser.getId());
        Assert.isTrue(!afterDeleteUser.isPresent(),"delete fail");
    }
}
```


## 配置文件
```properties
## DataSource配置
spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=123
spring.datasource.schema=classpath:db/schema.sql
spring.datasource.data=classpath:db/data.sql
spring.h2.console.enabled=true
spring.h2.console.path=/h2
## JPA配置
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 参考文档

[https://www.marcobehler.com/guides/spring-security](https://www.marcobehler.com/guides/spring-security)

[https://www.jitwxs.cn/categories/%E5%AE%89%E5%85%A8%E6%A1%86%E6%9E%B6/Spring-Security/](https://www.jitwxs.cn/categories/%E5%AE%89%E5%85%A8%E6%A1%86%E6%9E%B6/Spring-Security/)

[https://github.com/jitwxs/blog-sample/tree/master/SpringBoot/springboot_security](https://github.com/jitwxs/blog-sample/tree/master/SpringBoot/springboot_security)