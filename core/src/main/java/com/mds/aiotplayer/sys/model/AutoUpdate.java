/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.sys.model;

import com.mds.aiotplayer.common.model.DataEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.aiotplayer.common.model.JsonDateSerializer;

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
@Table(name="sys_autoupdate")
@Indexed
@XmlRootElement
public class AutoUpdate extends DataEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6886073908882200228L;
	private String strBatch;
    private String strDesc;
    private Date dtSent;
    private Date dtValidity;
    private Date dtPublish;
    private String strInternalNum;
 
    
    @JsonProperty(value = "strBatch")
    @Column(name="strBatch", nullable=false, length=50)
    @Field
    public String getBatch(){
        return this.strBatch;
    }
    
    public void setBatch(String strBatch){
        this.strBatch = strBatch;
    }
    
    @JsonProperty(value = "strDesc")
    @Column(name="strDesc", nullable=false, length=255)
    @Field
    public String getDesc(){
        return this.strDesc;
    }
    
    public void setDesc(String strDesc){
        this.strDesc = strDesc;
    }
    
    @JsonProperty(value = "dtSent")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtSent", length=19)
    @Field
    public Date getSent(){
        return this.dtSent;
    }
    
    public void setSent(Date dtSent){
        this.dtSent = dtSent;
    }
    
    @JsonProperty(value = "dtValidity")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtValidity", length=19)
    @Field
    public Date getValidity(){
        return this.dtValidity;
    }
    
    public void setValidity(Date dtValidity){
        this.dtValidity = dtValidity;
    }
    
    @JsonProperty(value = "dtPublish")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtPublish", length=19)
    @Field
    public Date getPublish(){
        return this.dtPublish;
    }
    
    public void setPublish(Date dtPublish){
        this.dtPublish = dtPublish;
    }
    
    @JsonProperty(value = "strInternalNum")
    @Column(name="strInternalNum", nullable=false, length=50)
    @Field
    public String getInternalNum(){
        return this.strInternalNum;
    }
    
    public void setInternalNum(String strInternalNum){
        this.strInternalNum = strInternalNum;
    }
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutoUpdate pojo = (AutoUpdate)o;
        return (new EqualsBuilder()
             .append(strBatch, pojo.strBatch)
             .append(strDesc, pojo.strDesc)
             .append(dtSent, pojo.dtSent)
             .append(dtValidity, pojo.dtValidity)
             .append(dtPublish, pojo.dtPublish)
             .append(strInternalNum, pojo.strInternalNum)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strBatch)
             .append(strDesc)
             .append(dtSent)
             .append(dtValidity)
             .append(dtPublish)
             .append(strInternalNum)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Id").append("='").append(getId()).append("', ");
        sb.append("strBatch").append("='").append(getBatch()).append("', ");
        sb.append("strDesc").append("='").append(getDesc()).append("', ");
        sb.append("dtSent").append("='").append(getSent()).append("', ");
        sb.append("dtValidity").append("='").append(getValidity()).append("', ");
        sb.append("dtPublish").append("='").append(getPublish()).append("', ");
        sb.append("strInternalNum").append("='").append(getInternalNum()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}