/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.servicemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.mds.kernel.MDSKernel;
import com.mds.kernel.MDSKernelManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Testing the kernel manager
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class MDSKernelManagerTest {

    MDSKernelManager kernelManager;
    MDSKernelImpl kernelImpl;

    @Before
    public void init() {
        kernelImpl = MDSKernelInit.getKernel(null);
        kernelImpl.start(); // init the kernel
        kernelManager = new MDSKernelManager();
    }

    @After
    public void destroy() {
        if (kernelImpl != null) {
            // cleanup the kernel
            kernelImpl.stop();
            kernelImpl.destroy();
        }
        kernelImpl = null;
        kernelManager = null;
    }

    /**
     * Test method for {@link com.mds.kernel.MDSKernelManager#getKernel()}.
     */
    @Test
    public void testGetKernel() {
        MDSKernel kernel = kernelManager.getKernel();
        assertNotNull(kernel);
        MDSKernel k2 = kernelManager.getKernel();
        assertNotNull(k2);
        assertEquals(kernel, k2);

        kernel = k2 = null;
    }

}
