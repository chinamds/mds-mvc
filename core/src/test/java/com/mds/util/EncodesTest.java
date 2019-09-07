/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import com.mds.common.utils.security.Encodes;
import com.mds.security.Digests;

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
