/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.exception;

import com.mds.aiotplayer.common.exception.BaseException;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-8 下午5:32
 * <p>Version: 1.0
 */
public class TemplateException extends BaseException {

    public TemplateException(final String code, final Object[] args) {
        super("notification", code, args);
    }
}
