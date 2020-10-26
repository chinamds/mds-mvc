/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.taglib.security;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JSP tag that renders the tag body only if the current user has executed a <b>successful</b> authentication attempt
 * <em>during their current session</em>.
 *
 * <p>This is more restrictive than the {@link UserTag}, which only
 * ensures the current user is known to the system, either via a current login or from Remember Me services,
 * which only makes the assumption that the current user is who they say they are, and does not guarantee it like
 * this tag does.
 *
 * <p>The logically opposite tag of this one is the {@link NotAuthenticatedTag}
 *
 * @since 0.2
 */
public class AuthenticatedTag extends SecureTag {

    //TODO - complete JavaDoc

    private static final Logger log = LoggerFactory.getLogger(AuthenticatedTag.class);

    public int onDoStartTag() throws JspException {
        if (getSubject() != null && getSubject().isAuthenticated()) {
            if (log.isTraceEnabled()) {
                log.trace("Subject exists and is authenticated.  Tag body will be evaluated.");
            }
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Subject does not exist or is not authenticated.  Tag body will not be evaluated.");
            }
            return TagSupport.SKIP_BODY;
        }
    }
}