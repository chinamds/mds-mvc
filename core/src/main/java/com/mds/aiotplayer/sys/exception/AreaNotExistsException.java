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
 * <p>Area: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:28
 * <p>Version: 1.0
 */
public class AreaNotExistsException extends BaseException {

    public AreaNotExistsException(String info) {
        super("area.not.exists", new Object[] { info });
    }
    
    public AreaNotExistsException() {
        super("area.not.exists"); 
    }
}
