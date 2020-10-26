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

import com.mds.aiotplayer.sys.util.UserAccount;

/**
 * @since 0.1
 */
public abstract class PermissionTag extends SecureTag {

    //TODO - complete JavaDoc

    private String name = null;

    public PermissionTag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void verifyAttributes() throws JspException {
        String permission = getName();

        if (permission == null || permission.length() == 0) {
            String msg = "The 'name' tag attribute must be set.";
            throw new JspException(msg);
        }
    }

    public int onDoStartTag() throws JspException {

        String p = getName();

        boolean show = showTagBody(p);
        if (show) {
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            return TagSupport.SKIP_BODY;
        }
    }

    protected boolean isPermitted(String p) {
        if (getSubject() != null && getSubject().getPrincipal() instanceof UserAccount)
        	return  ((UserAccount)getSubject().getPrincipal()).isPermitted(p);
        
        return false;
    }

    protected abstract boolean showTagBody(String p);

}
