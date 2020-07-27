// Created using LayerGen 3.5

package com.mds.aiotplayer.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.common.model.IdEntity;

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
@Table(name="cm_gallerycontrolsetting", uniqueConstraints = @UniqueConstraint(columnNames={"control_id", "setting_name"}))
@Indexed
@XmlRootElement
public class GalleryControlSetting extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9202382521379842106L;
    private String controlId;
    private String settingName;
    private String settingValue;
    
    public GalleryControlSetting() {
    	super();
    }
    
    public GalleryControlSetting(final String controlId, final String settingName, final String settingValue) {
    	this();
    	this.controlId = controlId;
    	this.settingName = settingName;
    	this.settingValue = settingValue;
    }
    
    @JsonProperty(value = "ControlId")
    @Column(name="control_id", nullable=false, length=350)
    @Field
    public String getControlId(){
        return this.controlId;
    }
    
    public void setControlId(String controlId){
        this.controlId = controlId;
    }
    
    @JsonProperty(value = "SettingName")
    @Column(name="setting_name", nullable=false, length=200)
    @Field
    public String getSettingName(){
        return this.settingName;
    }
    
    public void setSettingName(String settingName){
        this.settingName = settingName;
    }
    
    @JsonProperty(value = "SettingValue")
    @Column(name="setting_value", nullable=false)
    @Type(type="text")
    @Field
    public String getSettingValue(){
        return this.settingValue;
    }
    
    public void setSettingValue(String settingValue){
        this.settingValue = settingValue;
    }    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GalleryControlSetting pojo = (GalleryControlSetting)o;
        return (new EqualsBuilder()
             .append(controlId, pojo.controlId)
             .append(settingName, pojo.settingName)
             .append(settingValue, pojo.settingValue)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(controlId)
             .append(settingName)
             .append(settingValue)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("galleryControlSettingId").append("='").append(getId()).append("', ");
        sb.append("controlId").append("='").append(getControlId()).append("', ");
        sb.append("settingName").append("='").append(getSettingName()).append("', ");
        sb.append("settingValue").append("='").append(getSettingValue()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}