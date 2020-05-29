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