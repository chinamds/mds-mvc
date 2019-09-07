package com.mds.sys.exception;


/**
 * An exception that is thrown by classes wanting to trap unique 
 * constraint violations.  This is used to wrap Spring's 
 * DataIntegrityViolationException so it's checked in the web layer.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class OrganizationExistsException extends Exception {
    private static final long serialVersionUID = 4050482305178810162L;

    /**
     * Constructor for OrganizationExistsException.
     *
     * @param message exception message
     */
    public OrganizationExistsException(final String message) {
        super(message);
    }
}
