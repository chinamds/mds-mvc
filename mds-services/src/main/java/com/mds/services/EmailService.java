/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.services;

import javax.mail.Session;

/**
 * @author mwood
 */
public interface EmailService {
    /**
     * Provide a reference to the JavaMail session.
     *
     * @return JavaMail session
     */
    Session getSession();
}
