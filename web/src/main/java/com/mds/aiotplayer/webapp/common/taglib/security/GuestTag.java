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
 * JSP tag that renders the tag body if the current user <em>is not</em> known to the system, either because they
 * haven't logged in yet, or because they have no 'RememberMe' identity.
 *
 * <p>The logically opposite tag of this one is the {@link UserTag}.  Please read that class's JavaDoc as it explains
 * more about the differences between Authenticated/Unauthenticated and User/Guest semantic differences.
 *
 * @since 0.9
 */
public class GuestTag extends SecureTag {

    //TODO - complete JavaDoc

    private static final Logger log = LoggerFactory.getLogger(GuestTag.class);

    public int onDoStartTag() throws JspException {
        if (getSubject() == null || getSubject().getPrincipal() == null) {
            if (log.isTraceEnabled()) {
                log.trace("Subject does not exist or does not have a known identity (aka 'principal').  " +
                        "Tag body will be evaluated.");
            }
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Subject exists or has a known identity (aka 'principal').  " +
                        "Tag body will not be evaluated.");
            }
            return TagSupport.SKIP_BODY;
        }
    }

}
