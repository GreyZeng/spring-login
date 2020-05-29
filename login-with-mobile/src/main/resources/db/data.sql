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