/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.sys.model;

import com.mds.common.model.DataEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.common.model.JsonDateSerializer;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import  org.apache.commons.lang.builder.HashCodeBuilder;
import  org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="sys_setting", uniqueConstraints = @UniqueConstraint(columnNames={"settingmst_id", "uiType", "strGroup", "strName", "uiValueType"}))
@Indexed
@XmlRootElement
public class Setting extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7871174599525654630L;
	private SettingMst settingMst;
    private Short uiType;
    private String strGroup;
    private String strName;
    private Short uiValueType;
    private String strDefaValue;
    private String strValue;

    @ManyToOne
    @JoinColumn(name="settingmst_id", nullable=false)
    public SettingMst getSettingMst() {
		return settingMst;
	}

	public void setSettingMst(SettingMst settingMst) {
		this.settingMst = settingMst;
	}

	@JsonProperty(value = "uiType")
    @Column(name="uiType")
    @Field
    public Short getType(){
        return this.uiType;
    }
    
    public void setType(Short uiType){
        this.uiType = uiType;
    }
    
    @JsonProperty(value = "strGroup")
    @Column(name="strGroup", length=50)
    @Field
    public String getGroup(){
        return this.strGroup;
    }
    
    public void setGroup(String strGroup){
        this.strGroup = strGroup;
    }
    
    @JsonProperty(value = "strName")
    @Column(name="strName", length=50)
    @Field
    public String getName(){
        return this.strName;
    }
    
    public void setName(String strName){
        this.strName = strName;
    }
    
    @JsonProperty(value = "uiValueType")
    @Column(name="uiValueType")
    @Field
    public Short getValueType(){
        return this.uiValueType;
    }
    
    public void setValueType(Short uiValueType){
        this.uiValueType = uiValueType;
    }
    
    @JsonProperty(value = "strDefaValue")
    @Column(name="strDefaValue")
    @Type(type="text")
    @Field
    public String getDefaValue(){
        return this.strDefaValue;
    }
    
    public void setDefaValue(String strDefaValue){
        this.strDefaValue = strDefaValue;
    }
    
    @JsonProperty(value = "strValue")
    @Column(name="strValue")
    @Type(type="text")
    @Field
    public String getValue(){
        return this.strValue;
    }
    
    public void setValue(String strValue){
        this.strValue = strValue;
    }
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Setting pojo = (Setting)o;
        return (new EqualsBuilder()
             .append(uiType, pojo.uiType)
             .append(strGroup, pojo.strGroup)
             .append(strName, pojo.strName)
             .append(uiValueType, pojo.uiValueType)
             .append(strDefaValue, pojo.strDefaValue)
             .append(strValue, pojo.strValue)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(uiType)
             .append(strGroup)
             .append(strName)
             .append(uiValueType)
             .append(strDefaValue)
             .append(strValue)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Id").append("='").append(getId()).append("', ");
        sb.append("uiType").append("='").append(getType()).append("', ");
        sb.append("strGroup").append("='").append(getGroup()).append("', ");
        sb.append("strName").append("='").append(getName()).append("', ");
        sb.append("uiValueType").append("='").append(getValueType()).append("', ");
        sb.append("strDefaValue").append("='").append(getDefaValue()).append("', ");
        sb.append("strValue").append("='").append(getValue()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}