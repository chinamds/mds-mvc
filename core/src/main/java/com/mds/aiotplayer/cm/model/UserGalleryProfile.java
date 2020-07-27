// Created using LayerGen 3.5

package com.mds.aiotplayer.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
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
import java.util.List;
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
@Table(name="cm_usergalleryprofile", uniqueConstraints = @UniqueConstraint(columnNames={"gallery_id", "user_name", "setting_name"}))
@Indexed
@XmlRootElement
public class UserGalleryProfile extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1048637490542595625L;
	private String userName;
    private String settingName;
    private String settingValue;
     
    private Gallery gallery;
    private List<Album> albums = Lists.newArrayList(); // albums
    
    @ManyToOne
    @JoinColumn(name="gallery_id", nullable=false)
    public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}
	
    @JsonProperty(value = "UserName")
    @Column(name="user_name", nullable=false, length=256)
    @Field
    public String getUserName(){
        return this.userName;
    }
    
    public void setUserName(String userName){
        this.userName = userName;
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

        UserGalleryProfile pojo = (UserGalleryProfile)o;
        return (new EqualsBuilder()
             .append(userName, pojo.userName)
             .append(settingName, pojo.settingName)
             .append(settingValue, pojo.settingValue)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(userName)
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
        sb.append("profileId").append("='").append(getId()).append("', ");
        sb.append("userName").append("='").append(getUserName()).append("', ");
        sb.append("settingName").append("='").append(getSettingName()).append("', ");
        sb.append("settingValue").append("='").append(getSettingValue()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}