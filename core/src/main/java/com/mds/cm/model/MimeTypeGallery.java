// Created using LayerGen 3.5

package com.mds.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.common.model.JsonDateSerializer;
import com.mds.common.model.IdEntity;

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
@Table(name="cm_mimetypegallery", uniqueConstraints = @UniqueConstraint(columnNames={"gallery_id", "mime_type_id"}))
@Indexed
@XmlRootElement
public class MimeTypeGallery extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 668061181932172575L;
    private boolean isEnabled;
    
    private Gallery gallery;
    private MimeType mimeType;
    
    public MimeTypeGallery(){
		super();
	}
    
    public MimeTypeGallery(final Gallery gallery, final MimeType mimeType, final boolean isEnabled){
		super();
		this.gallery = gallery;
		this.mimeType = mimeType;
		this.isEnabled = isEnabled;
	}
    
    @ManyToOne
    @JoinColumn(name="gallery_id", nullable=false)
    public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}
        
    @ManyToOne
    @JoinColumn(name="mime_type_id", nullable=false)
    public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}
    
    @JsonProperty(value = "IsEnabled")
    @Column(name="is_enabled", nullable=false)
    @Field
    public boolean isIsEnabled(){
        return this.isEnabled;
    }
    
    public void setIsEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MimeTypeGallery pojo = (MimeTypeGallery)o;
        return (new EqualsBuilder()
        	 .append(id, pojo.id)
             .append(isEnabled, pojo.isEnabled)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
        	 .append(id)
             .append(isEnabled)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("mimeTypeGalleryId").append("='").append(getId()).append("', ");
        sb.append("isEnabled").append("='").append(isIsEnabled()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}