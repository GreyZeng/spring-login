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