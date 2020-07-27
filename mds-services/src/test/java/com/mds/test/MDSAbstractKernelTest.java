/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mds.kernel.MDSKernel;
import com.mds.kernel.MDSKernelManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * This is an abstract class which makes it easier to test things that use the MDS Kernel,
 * this will start and stop the kernel at the beginning of the group of tests that are
 * in the junit test class which extends this
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public abstract class MDSAbstractKernelTest extends MDSAbstractTest {

    @BeforeClass
    public static void initKernel() {
        _initializeKernel();
        assertNotNull(kernelImpl);
        assertTrue(kernelImpl.isRunning());
        assertNotNull(kernel);
    }

    @AfterClass
    public static void destroyKernel() {
        _destroyKernel();
    }

    /**
     * Test method for {@link com.mds.kernel.MDSKernelManager#getKernel()}.
     */
    @Test
    public void testKernelIsInitializedAndWorking() {
        assertNotNull(kernel);
        assertTrue(kernel.isRunning());
        MDSKernel k2 = new MDSKernelManager().getKernel();
        assertNotNull(k2);
        assertEquals(kernel, k2);
    }

}
