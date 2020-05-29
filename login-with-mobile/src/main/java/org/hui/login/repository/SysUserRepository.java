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