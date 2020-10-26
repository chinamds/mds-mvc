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
 * JSP tag that renders the tag body only if the current user has <em>not</em> executed a successful authentication
 * attempt <em>during their current session</em>.
 *
 * <p>The logically opposite tag of this one is the {@link org.apache.shiro.web.tags.AuthenticatedTag}.
 *
 * @since 0.2
 */
public class NotAuthenticatedTag extends SecureTag {

    //TODO - complete JavaDoc

    private static final Logger log = LoggerFactory.getLogger(NotAuthenticatedTag.class);

    public int onDoStartTag() throws JspException {
        if (getSubject() == null || !getSubject().isAuthenticated()) {
            if (log.isTraceEnabled()) {
                log.trace("Subject does not exist or is not authenticated.  Tag body will be evaluated.");
            }
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Subject exists and is authenticated.  Tag body will not be evaluated.");
            }
            return TagSupport.SKIP_BODY;
        }
    }
}