// Created using LayerGen 3.5

package com.mds.hrm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.common.model.DataEntity;
import com.mds.common.model.JsonDateSerializer;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
@Table(name="hrm_staffposition")
@Indexed
@XmlRootElement
public class StaffPosition extends DataEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Staff staff;
    private Date dateFrom;
    private Date dateTo;
    private Position position;
    private String remark;
    private String title;
     
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="staff_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
    public Staff getStaff(){
        return this.staff;
    }
    
    public void setStaff(Staff staff){
        this.staff = staff;
    }
    
    @JsonProperty(value = "DateFrom")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DateFrom", length=19)
    @Field
    public Date getDateFrom(){
        return this.dateFrom;
    }
    
    public void setDateFrom(Date dateFrom){
        this.dateFrom = dateFrom;
    }
    
    @JsonProperty(value = "DateTo")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DateTo", length=19)
    @Field
    public Date getDateTo(){
        return this.dateTo;
    }
    
    public void setDateTo(Date dateTo){
        this.dateTo = dateTo;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="position_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
    public Position getPosition(){
        return this.position;
    }
    
    public void setPosition(Position position){
        this.position = position;
    }
        
    @JsonProperty(value = "Remark")
    @Column(name="Remark")
    @Type(type="text")
    @Field
    public String getRemark(){
        return this.remark;
    }
    
    public void setRemark(String remark){
        this.remark = remark;
    }
    
    @JsonProperty(value = "Title")
    @Column(name="Title", length=200)
    @Field
    public String getTitle(){
        return this.title;
    }
    
    public void setTitle(String title){
        this.title = title;
    }
    
    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaffPosition pojo = (StaffPosition)o;
        return (new EqualsBuilder()
             .append(dateFrom, pojo.dateFrom)
             .append(dateTo, pojo.dateTo)
             .append(remark, pojo.remark)
             .append(title, pojo.title)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(dateFrom)
             .append(dateTo)
             .append(remark)
             .append(title)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("dateFrom").append("='").append(getDateFrom()).append("', ");
        sb.append("dateTo").append("='").append(getDateTo()).append("', ");
        sb.append("remark").append("='").append(getRemark()).append("', ");
        sb.append("title").append("='").append(getTitle()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}