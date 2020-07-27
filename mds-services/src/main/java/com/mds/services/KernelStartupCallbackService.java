/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.services;

/**
 * Interface whos implementations will be called when the kernel startup is completed.
 *
 * @author kevinvandevelde at atmire.com
 */
public interface KernelStartupCallbackService {

    public void executeCallback();
}
