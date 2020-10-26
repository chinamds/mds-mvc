/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

import java.io.Serializable;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;


/**
 * Base class for Model objects. Child objects should implement toString(),
 * equals() and hashCode().
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@SuppressWarnings("serial")
public abstract class BaseObject implements Serializable {    

    /**
     * Returns a multi-line String with key=value pairs.
     * @return a String representation of this class.
     */
    public abstract String toString();

    /**
     * Compares object equality. When using Hibernate, the primary key should
     * not be a part of this comparison.
     * @param o object to compare to
     * @return true/false based on equality tests
     */
    public abstract boolean equals(Object o);

    /**
     * When you override equals, you should override hashCode. See "Why are
     * equals() and hashCode() importation" for more information:
     * http://www.hibernate.org/109.html
     * @return hashCode
     */
    public abstract int hashCode();
    
    /**
     * Copy the property values of the given source bean into the this bean.
     * 
     * 
     * 
     */
    public abstract void copyFrom(Object source);
    
    //@PrePersist
	public void prePersist(){
	}
	
	//@PreUpdate
	public void preUpdate(){
		/*if (source != null){
			copyFrom(source);
		}*/
	}
	
	public void beforeUpdate(Object source){
		if (source != null){
			copyFrom(source);
		}
	}
}
