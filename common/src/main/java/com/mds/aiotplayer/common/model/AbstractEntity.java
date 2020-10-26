/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 抽象实体基类，如果主键是数据库端自动生成 请使用{@link BaseEntity}，如果是Oracle 请使用{@link BaseOracleEntity}
 * <p/>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-20 下午8:38
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
@XmlTransient
public abstract class AbstractEntity<ID extends Serializable> extends BaseObject implements Persistable<ID> {

	//@Transient
	//@XmlTransient
    //@JsonIgnore
    public abstract ID getId();

    /**
     * Sets the id of the entity.
     *
     * @param id the id to set
     */
    public abstract void setId(final ID id);

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.domain.Persistable#isNew()

     */
    @Transient
	@XmlTransient
    @JsonIgnore
    public boolean isNew() {
        return null == getId();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        AbstractEntity<?> that = (AbstractEntity<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    
    @Override
    public void copyFrom(Object obj) {
        try {
			BeanUtils.copyProperties(this, obj);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
