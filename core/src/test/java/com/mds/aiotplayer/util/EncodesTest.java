/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import com.mds.aiotplayer.common.utils.security.Encodes;
import com.mds.aiotplayer.security.Digests;

public class EncodesTest {
	private final Logger log = LoggerFactory.getLogger(DateUtilTest.class);

    @Test
	public void testEntryptPassword() {
		String plainPassword = "admin";
		byte[] salt = Digests.generateSalt(HelperFunctions.SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HelperFunctions.HASH_INTERATIONS);
		String password = Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
		if (log.isDebugEnabled()) {
            log.debug("Entrypted Password is: " + password);
        }
	}
}
