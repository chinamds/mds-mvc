/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.taglib.security;

import javax.servlet.jsp.JspException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JSP tag that renders the tag body if the current user known to the system, either from a successful login attempt
 * (not necessarily during the current session) or from 'RememberMe' services.
 *
 * <p><b>Note:</b> This is <em>less</em> restrictive than the <code>AuthenticatedTag</code> since it only assumes
 * the user is who they say they are, either via a current session login <em>or</em> via Remember Me services, which
 * makes no guarantee the user is who they say they are.  The <code>AuthenticatedTag</code> however
 * guarantees that the current user has logged in <em>during their current session</em>, proving they really are
 * who they say they are.
 *
 * <p>The logically opposite tag of this one is the {@link org.apache.shiro.web.tags.GuestTag}.
 *
 * @since 0.9
 */
public class UserTag extends SecureTag {

    //TODO - complete JavaDoc

    private static final Logger log = LoggerFactory.getLogger(UserTag.class);

    public int onDoStartTag() throws JspException {
        if (getSubject() != null && getSubject().getPrincipal() != null) {
            if (log.isTraceEnabled()) {
                log.trace("Subject has known identity (aka 'principal').  " +
                        "Tag body will be evaluated.");
            }
            return EVAL_BODY_INCLUDE;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Subject does not exist or have a known identity (aka 'principal').  " +
                        "Tag body will not be evaluated.");
            }
            return SKIP_BODY;
        }
    }

}
