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
 * <p>MenuFunction: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:28
 * <p>Version: 1.0
 */
public class MenuFunctionNotExistsException extends BaseException {

    public MenuFunctionNotExistsException(String info) {
        super("menuFunction.not.exists", new Object[] { info });
    }
    
    public MenuFunctionNotExistsException() {
        super("menuFunction.not.exists"); 
    }
}
