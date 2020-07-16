/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">MDS</a> All rights reserved.
 */
package com.mds.sys.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.google.common.collect.Lists;
import com.mds.cm.model.GalleryMapping;
import com.mds.common.model.IdEntity;
import com.mds.common.model.DataEntity;

/**
 * time schedule task: specified beanName.beanMethod or beanClass.beanMethod to  perform
 * <p>User: John Lee
 * <p>Date: 2017/8/18 19:43
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_task_definition")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class TaskDefinition extends DataEntity {

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7804612025048869059L;

	private String name;

    /**
     * cron express
     */
    private String cron;


    /**
     * The class name to perform 
     */
    private String beanClass;

    /**
     * the bean name of specified task to perform
     */
    private String beanName;

    /**
     * method name of the bean to perform
     */
    private String methodName;

    /**
     * is start
     */
    private Boolean start = Boolean.FALSE;

    /**
     * description
     */
    private String description;

    @Column(name = "name", length=100, nullable=false, unique=true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "cron", length=1024)
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Column(name = "bean_class", length=1024)
    public String getBeanClass() {
        return beanClass;
    }
    
    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    @Column(name = "bean_name", length=1024)
    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Column(name = "method_name", length=1024)
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Column(name = "is_start")
    public Boolean getStart() {
        return start;
    }

    public void setStart(Boolean start) {
        this.start = start;
    }

    @Column(name = "description", length=1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
