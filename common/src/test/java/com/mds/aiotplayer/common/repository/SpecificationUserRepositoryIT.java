/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.repository;

import com.mds.aiotplayer.common.model.User;
import com.mds.aiotplayer.common.test.BaseUserIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.junit.Assert.assertNotNull;

import java.util.Optional;

/**
 * <p>测试DDD Specification，Repository必须继承JpaSpecificationExecutor</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午5:02
 * <p>Version: 1.0
 */
public class SpecificationUserRepositoryIT extends BaseUserIT {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Before
    public void setUp() {
        user = createUser();
    }

    @After
    public void tearDown() {
        user = null;
    }

    @Test
    public void test() {

        userRepository.save(user);

        Specification<User> spec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("id"), user.getId());
            }
        };

        clear();

        Optional<User> optUser = userRepository.findOne(spec);
        User dbUser = optUser.isPresent()? optUser.get() : null;
        assertNotNull(dbUser);

    }

}
