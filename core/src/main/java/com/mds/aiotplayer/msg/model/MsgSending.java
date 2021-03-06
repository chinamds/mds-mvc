/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.msg.model;

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
@Table(name="msg_msgsending", uniqueConstraints = @UniqueConstraint(columnNames={"strUniqueName", "strAHName"}))
@Indexed
@XmlRootElement
public class MsgSending extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2293262501214020285L;
	private String strUniqueName;
    private String strAHName;
    private Date dtSentTime;
    private short nAction;
   
    @JsonProperty(value = "strUniqueName")
    @Column(name="strUniqueName", nullable=false, length=50)
    @Field
    public String getUniqueName(){
        return this.strUniqueName;
    }
    
    public void setUniqueName(String strUniqueName){
        this.strUniqueName = strUniqueName;
    }
    
    @JsonProperty(value = "strAHName")
    @Column(name="strAHName", nullable=false, length=50)
    @Field
    public String getAHName(){
        return this.strAHName;
    }
    
    public void setAHName(String strAHName){
        this.strAHName = strAHName;
    }
    
    @JsonProperty(value = "dtSentTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtSentTime", nullable=false, length=19)
    @Field
    public Date getSentTime(){
        return this.dtSentTime;
    }
    
    public void setSentTime(Date dtSentTime){
        this.dtSentTime = dtSentTime;
    }
    
    @JsonProperty(value = "nAction")
    @Column(name="nAction", nullable=false)
    @Field
    public short getAction(){
        return this.nAction;
    }
    
    public void setAction(short nAction){
        this.nAction = nAction;
    }

    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MsgSending pojo = (MsgSending)o;
        return (new EqualsBuilder()
             .append(strUniqueName, pojo.strUniqueName)
             .append(strAHName, pojo.strAHName)
             .append(dtSentTime, pojo.dtSentTime)
             .append(nAction, pojo.nAction)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strUniqueName)
             .append(strAHName)
             .append(dtSentTime)
             .append(nAction)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("uiID").append("='").append(getId()).append("', ");
        sb.append("strUniqueName").append("='").append(getUniqueName()).append("', ");
        sb.append("strAHName").append("='").append(getAHName()).append("', ");
        sb.append("dtSentTime").append("='").append(getSentTime()).append("', ");
        sb.append("nAction").append("='").append(getAction()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}