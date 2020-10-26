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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mds.aiotplayer.sys.util.UserAccount;

/**
 * @since 0.1
 */
public abstract class SecureTag extends TagSupport {

    //TODO - complete JavaDoc

    private static final Logger log = LoggerFactory.getLogger(SecureTag.class);

    public SecureTag() {
    }

    protected Authentication getSubject() {
    	SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx != null){		
			return ctx.getAuthentication();
		}
		        
        return null;
    }

    protected void verifyAttributes() throws JspException {
    }

    public int doStartTag() throws JspException {

        verifyAttributes();

        return onDoStartTag();
    }

    public abstract int onDoStartTag() throws JspException;
}
