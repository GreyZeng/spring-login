# Spring Security实现用户名密码登录

## 环境

- JDK 1.8
- Spring Boot 2.3.0.RELEASE
- Maven 3.6.1
- H2数据库

## 用户名密码登录

首先，我们用Spring Security实现用户输入用户名密码登录验证并获取相应权限。

### E-R图

![E-R](https://img2020.cnblogs.com/blog/683206/202005/683206-20200520194043917-1557519493.png)



完整建表语句

> 因为是测试程序，所以用H2数据库来测试。SQL脚本在resouces/db目录下，项目启动后会自动初始化脚本，无需手动执行。

schema.sql

```sql
DROP TABLE IF EXISTS `SYS_ROLE`;
DROP TABLE IF EXISTS `SYS_USER_ROLE`;
DROP TABLE IF EXISTS `SYS_USER`;

create table SYS_ROLE
(
    ID   INT not null primary key,
    NAME VARCHAR(255) not null
);

create table SYS_USER
(
    ID       INT not null primary key,
    NAME     VARCHAR not null,
    PASSWORD VARCHAR(255) not null
);

create table SYS_USER_ROLE
(
    USER_ID INT not null,
    ROLE_ID INT not null,
    constraint pk_1 primary key (ROLE_ID, USER_ID),
    constraint fk_1 foreign key (ROLE_ID) references SYS_ROLE (ID) on update cascade on delete cascade,
    constraint fk_2 foreign key (USER_ID) references SYS_USER (ID) on update cascade on delete cascade
);
```

data.sql

```sql
INSERT INTO `SYS_ROLE` (`ID`, `NAME`) VALUES (1, 'ADMIN'),(2, 'USER');
INSERT INTO `SYS_USER` (`ID`, `NAME`, `PASSWORD`) VALUES (1, 'super', '888888'), (2, 'jack', '666666'), (3, 'lucy', '999999');
INSERT INTO `SYS_USER_ROLE` (`USER_ID`, `ROLE_ID`) VALUES (1, 1),(2, 2),(3, 2);
```



准备好建表语句后，完成实体类的编写

SysRole.java

```java
package org.hui.login.model;

/**
 * 用户角色
 * @author zenghui
 * @date 2020-05-20
 */
public class SysRole {
    private Integer id;
    private String name;
  
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}

```



SysUser.java

```java
package org.hui.login.model;

/**
 * 用户基本信息
 * @author zenghui
 * @date 2020-05-20
 */
public class SysUser  {
    private Integer id;
    private String name;
    private String password;

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}

```



SysUserRole.java

```java
package org.hui.login.model;

/**
 * 角色和用户对应关系
 * @author zenghui
 * @date 2020-05-20
 */
public class SysUserRole  {
    private Integer userId;
    private Integer roleId;

    public Integer getUserId() {return userId;}
    public void setUserId(Integer userId) {this.userId = userId;}
    public Integer getRoleId() {return roleId;}
    public void setRoleId(Integer roleId) {this.roleId = roleId;}
}

```

以上，完成了数据层的设计。



### POM依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.3.0.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>example</groupId>
	<artifactId>spring-login</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>spring-login</name>
	<description>Spring Security to implements Login</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>2.1.2</version>
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



### 配置文件

application.properties

```properties
spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=123
#设置SQL脚本的位置，resources/db目录下，如果不设置的话，默认就在resources目录下
spring.datasource.schema=classpath:db/schema.sql
spring.datasource.data=classpath:db/data.sql
#H2控制台启用
spring.h2.console.enabled=true
#访问H2的URL
spring.h2.console.path=/h2

# 下划线转化为驼峰命名
mybatis.configuration.map-underscore-to-camel-case=true
```



### Mapper

采用了Mybatis来操作数据库。

准备好需要的几个Mapper，如下

SysRoleMapper.java

```java
package org.hui.login.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.hui.login.model.SysRole;
/**
 * @author zenghui
 * @date 2020-05-20
 */
@Mapper
public interface SysRoleMapper {
    @Select("SELECT * FROM sys_role WHERE id = #{id}")
    SysRole selectById(Integer id);
}

```



SysUserMapper.java

```java
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
```



SysUserRoleMapper.java

```java
package org.hui.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.hui.login.model.SysUserRole;

import java.util.List;
/**
 * @author zenghui
 * @date 2020-05-20
 */
@Mapper
public interface SysUserRoleMapper {
    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId}")
    List<SysUserRole> listByUserId(Integer userId);
}
```

### Service设计

> Spring Security提供了一个UserDetailsService接口，我们需要实现这个接口的loadUserByUsername方法，用于用户信息的获取，如果存在用户，则把用户的密码获取出来，如果不存在这个用户，直接抛异常。

我们新建一个SysUserDetailService.java

```java
package org.hui.login.service;

import org.hui.login.mapper.SysRoleMapper;
import org.hui.login.mapper.SysUserMapper;
import org.hui.login.mapper.SysUserRoleMapper;
import org.hui.login.model.SysRole;
import org.hui.login.model.SysUser;
import org.hui.login.model.SysUserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zenghui
 * @date 2020-05-20
 */
@Service
public class SysUserDetailService implements UserDetailsService {
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    public SysUserDetailService(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper, SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 从数据库中取出用户信息
        SysUser user = sysUserMapper.selectByName(username);

        // 判断用户是否存在
        if(user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 添加权限
        List<SysUserRole> userRoles = sysUserRoleMapper.listByUserId(user.getId());
        for (SysUserRole userRole : userRoles) {
            SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        // 返回UserDetails实现类
        return new User(user.getName(), user.getPassword(), authorities);

    }
}

```

如果用户存在，则会进行密码的校验，方便起见，密码我们假设储存为明文，实际上密码有很多加密的策略，这个后序可以自己配置，密码校验在SecurityConfig这个类中，代码如下：

```java
package org.hui.login.config;

import org.hui.login.service.SysUserDetailService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author zenghui
 * @date 2020-05-20
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final SysUserDetailService sysUserDetailService;

    public SecurityConfig(SysUserDetailService sysUserDetailService) {
        this.sysUserDetailService = sysUserDetailService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(sysUserDetailService).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }
            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and().formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll()
                .and().logout().permitAll();

        // 关闭CSRF跨域
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web)  {
        web.ignoring().antMatchers("/css/**", "/js/**");
    }
}

```



以上便完成了所有后端代码的编写，接下来，完成controller和前端页面的编写。

### HTML

在resources的static目录下，新建两个html文件

login.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>登录页</title>
</head>
<body>
<form action="/login" method="post">
    用户名：<input type="text" name="username">
    密码：<input type="password" name="password">
    <button type="submit">登录</button>
</form>
</body>
</html>
```



home.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>主页</title>
</head>
<body>
  登录成功
</body>
</html>
```

### Controller

新建一个LoginController，用于接收前端请求

```java
package org.hui.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zenghui
 * @date 2020-05-20
 */
@Controller
public class LoginController {
    @RequestMapping("/")
    public String home() {
        return "home.html";
    }
    @RequestMapping("/login")
    public String login() {
        return "login.html";
    }
}

```



### 启动

运行以下主程序

```java
package org.hui.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zenghui
 * @date 2020-05-20
 */
@SpringBootApplication
public class LoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }
}

```

访问 http://localhost:8080/login

显示登录页面，输入用户名密码，点击登录，即可看到效果


## 完整代码

[Github](https://github.com/GreyZeng/spring-login/tree/master/login-with-password)

[Gitee](https://gitee.com/greyzeng/spring-login/tree/master/login-with-password)

## 参考文档&代码

[https://www.jitwxs.cn/categories/%E5%AE%89%E5%85%A8%E6%A1%86%E6%9E%B6/Spring-Security/](https://www.jitwxs.cn/categories/%E5%AE%89%E5%85%A8%E6%A1%86%E6%9E%B6/Spring-Security/)

[https://github.com/jitwxs/blog-sample/tree/master/SpringBoot/springboot_security](https://github.com/jitwxs/blog-sample/tree/master/SpringBoot/springboot_security)