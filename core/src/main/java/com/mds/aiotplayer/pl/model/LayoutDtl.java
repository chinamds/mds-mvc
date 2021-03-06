/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.pl.model;

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
@Table(name="pl_layoutdtl", uniqueConstraints = @UniqueConstraint(columnNames={"layoutmst_id", "nZoneID"}))
@Indexed
@XmlRootElement
public class LayoutDtl extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8096637904164706226L;
	private LayoutMst layoutMst;
    private Short nZoneID;
    private Integer nLeft;
    private Integer nTop;
    private Integer nRight;
    private Integer nBottom;
    private Short alpha;
    private Short nLevel;
         
    /**
	 * @return the layoutMst
	 */
    @ManyToOne
    @JoinColumn(name="layoutmst_id", nullable=false)
	public LayoutMst getLayoutMst() {
		return layoutMst;
	}

	/**
	 * @param layoutMst the layoutMst to set
	 */
	public void setLayoutMst(LayoutMst layoutMst) {
		this.layoutMst = layoutMst;
	}

	@JsonProperty(value = "nZoneID")
    @Column(name="nZoneID")
    @Field
    public Short getZoneId(){
        return this.nZoneID;
    }
    
    public void setZoneId(Short nZoneID){
        this.nZoneID = nZoneID;
    }
    
    @JsonProperty(value = "nLeft")
    @Column(name="nLeft")
    @Field
    public Integer getLeft(){
        return this.nLeft;
    }
    
    public void setLeft(Integer nLeft){
        this.nLeft = nLeft;
    }
    
    @JsonProperty(value = "nTop")
    @Column(name="nTop")
    @Field
    public Integer getTop(){
        return this.nTop;
    }
    
    public void setTop(Integer nTop){
        this.nTop = nTop;
    }
    
    @JsonProperty(value = "nRight")
    @Column(name="nRight")
    @Field
    public Integer getRight(){
        return this.nRight;
    }
    
    public void setRight(Integer nRight){
        this.nRight = nRight;
    }
    
    @JsonProperty(value = "nBottom")
    @Column(name="nBottom")
    @Field
    public Integer getBottom(){
        return this.nBottom;
    }
    
    public void setBottom(Integer nBottom){
        this.nBottom = nBottom;
    }
    
    @JsonProperty(value = "alpha")
    @Column(name="alpha")
    @Field
    public Short getAlpha(){
        return this.alpha;
    }
    
    public void setAlpha(Short alpha){
        this.alpha = alpha;
    }
    
    @JsonProperty(value = "nLevel")
    @Column(name="nLevel")
    @Field
    public Short getLevel(){
        return this.nLevel;
    }
    
    public void setLevel(Short nLevel){
        this.nLevel = nLevel;
    }
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayoutDtl pojo = (LayoutDtl)o;
        return (new EqualsBuilder()
             .append(getLayoutMst().getId(), pojo.getLayoutMst().getId())
             .append(nZoneID, pojo.nZoneID)
             .append(nLeft, pojo.nLeft)
             .append(nTop, pojo.nTop)
             .append(nRight, pojo.nRight)
             .append(nBottom, pojo.nBottom)
             .append(alpha, pojo.alpha)
             .append(nLevel, pojo.nLevel)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(layoutMst.getId())
             .append(nZoneID)
             .append(nLeft)
             .append(nTop)
             .append(nRight)
             .append(nBottom)
             .append(alpha)
             .append(nLevel)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("uiID").append("='").append(getId()).append("', ");
        sb.append("layoutmst_id").append("='").append(getLayoutMst().getId()).append("', ");
        sb.append("nZoneID").append("='").append(getZoneId()).append("', ");
        sb.append("nLeft").append("='").append(getLeft()).append("', ");
        sb.append("nTop").append("='").append(getTop()).append("', ");
        sb.append("nRight").append("='").append(getRight()).append("', ");
        sb.append("nBottom").append("='").append(getBottom()).append("', ");
        sb.append("alpha").append("='").append(getAlpha()).append("', ");
        sb.append("nLevel").append("='").append(getLevel()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}