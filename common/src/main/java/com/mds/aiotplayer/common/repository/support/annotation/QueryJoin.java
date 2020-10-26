/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.repository.support.annotation;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.*;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-10-18
 * <p>Version: 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryJoin {

    /**
     * 连接的名字
     * @return
     */
    String property();

    JoinType joinType();

}
