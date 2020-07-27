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
import static org.junit.Assert.fail;

import com.mds.kernel.MDSKernel;
import com.mds.kernel.MDSKernelManager;
import com.mds.utils.MDSPlus;
import org.junit.Test;

/**
 * Make sure the MDS static cover works
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class MDSTest {

    @Test
    public void testMDSObject() {
        try {
            MDSPlus mds = new MDSPlus();
            mds.getServiceManager();
            fail("should have thrown exception");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }

        MDSKernelImpl kernelImpl = MDSKernelInit.getKernel(null);
        kernelImpl.start(); // triggers the init
        MDSKernel kernel = new MDSKernelManager().getKernel();
        assertNotNull(kernel);
        assertEquals(kernel, kernelImpl);

        MDSPlus mds = new MDSPlus();
        Object o = mds.getServiceManager();
        assertNotNull(o);
        assertEquals(o, kernel.getServiceManager());

        // repeat a few times
        o = mds.getServiceManager();
        assertNotNull(o);
        assertEquals(o, kernel.getServiceManager());

        o = mds.getServiceManager();
        assertNotNull(o);
        assertEquals(o, kernel.getServiceManager());

        MDSPlus mds2 = new MDSPlus();
        assertNotNull(mds2.getServiceManager());
        assertEquals(mds.getServiceManager(), mds2.getServiceManager());

        // REPEAT
        kernel = new MDSKernelManager().getKernel();

        o = mds.getServiceManager();
        assertNotNull(o);
        assertEquals(o, kernel.getServiceManager());

        // repeat a few times
        o = mds.getServiceManager();
        assertNotNull(o);
        assertEquals(o, kernel.getServiceManager());

        o = mds.getServiceManager();
        assertNotNull(o);
        assertEquals(o, kernel.getServiceManager());

        //trash the references
        kernelImpl.destroy();
        kernelImpl = null;
        kernel = null;
        mds = null;
        mds2 = null;
        o = null;
    }

/*********
 @Test public void testStaticCover() {
 try {
 MDS.getServiceManager();
 fail("should have thrown exception");
 } catch (IllegalStateException e) {
 assertNotNull(e.getMessage());
 }

 MDSKernelImpl kernelImpl = MDSKernelInit.getKernel(null);
 kernelImpl.start(); // triggers the init
 MDSKernel kernel = new MDSKernelManager().getKernel();
 assertNotNull(kernel);
 assertEquals(kernel, kernelImpl);

 Object o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 // repeat a few times
 o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 // REPEAT
 kernel = new MDSKernelManager().getKernel(); // init the kernel

 o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 // repeat a few times
 o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 kernelImpl.destroy(); // cleanup the kernel

 try {
 MDS.getServiceManager();
 fail("should have thrown exception");
 } catch (IllegalStateException e) {
 assertNotNull(e.getMessage());
 }

 }

 @Test public void testRestarts() {
 try {
 MDS.getServiceManager();
 fail("should have thrown exception");
 } catch (IllegalStateException e) {
 assertNotNull(e.getMessage());
 }

 MDSKernelImpl kernelImpl = MDSKernelInit.getKernel(null);
 MDSKernelImpl kernelImpl2 = MDSKernelInit.getKernel(null);
 assertEquals(kernelImpl, kernelImpl2);
 kernelImpl2 = null;

 kernelImpl.start(); // triggers the init
 MDSKernel kernel = new MDSKernelManager().getKernel();
 assertNotNull(kernel);
 assertEquals(kernel, kernelImpl);

 Object o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 kernelImpl.stop(); // stop the kernel

 try {
 MDS.getServiceManager();
 fail("should have thrown exception");
 } catch (IllegalStateException e) {
 assertNotNull(e.getMessage());
 }

 kernelImpl.start(); // triggers the init
 kernel = new MDSKernelManager().getKernel();

 o = MDS.getServiceManager();
 assertNotNull(o);
 assertEquals(o, kernel.getServiceManager());

 kernelImpl.stop(); // stop the kernel

 try {
 MDS.getServiceManager();
 fail("should have thrown exception");
 } catch (IllegalStateException e) {
 assertNotNull(e.getMessage());
 }

 kernelImpl2 = MDSKernelInit.getKernel(null);
 // check it is the same
 assertEquals(kernelImpl, kernelImpl2);

 kernelImpl2.start(); // triggers the init
 MDSKernel kernel2 = new MDSKernelManager().getKernel();
 assertNotNull(kernel2);
 assertEquals(kernel2, kernelImpl2);

 assertEquals(kernel, kernel2);

 Object o2 = MDS.getServiceManager();
 assertNotNull(o2);
 assertEquals(o2, kernel2.getServiceManager());

 // now try to startup the kernel again (should not start again)
 kernelImpl.start();

 assertEquals(kernelImpl2.getServiceManager(), kernelImpl.getServiceManager());

 kernelImpl2.destroy(); // cleanup the kernel
 kernelImpl.destroy(); // should not fail

 try {
 MDS.getServiceManager();
 fail("should have thrown exception");
 } catch (IllegalStateException e) {
 assertNotNull(e.getMessage());
 }

 }
 ******/

}
