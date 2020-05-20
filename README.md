# Spring Security实现多种登录方式

## 登录方式

- 用户名密码登录
- 短信验证码登录
- 微信登录

## 环境

- JDK 1.8
- Spring Boot 2.3.0.RELEASE
- Maven 3.6.1

## 用户名密码登录

首先，我们用Spring Security实现用户输入用户名密码登录验证并获取相应权限。

### 初始化脚本

> 因为是测试程序，所以用H2数据库来测试。SQL脚本在resouces/db目录下，项目启动后会自动初始化脚本，无需手动执行。

E-R图

![E-R](https://img2020.cnblogs.com/blog/683206/202005/683206-20200520194043917-1557519493.png)



完整建表语句

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



