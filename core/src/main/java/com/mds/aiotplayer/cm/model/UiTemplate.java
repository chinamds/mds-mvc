// Created using LayerGen 3.5

package com.mds.aiotplayer.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.IdEntity;
import com.mds.aiotplayer.core.UiTemplateType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Table(name="cm_uitemplate" )
@Indexed
@XmlRootElement
public class UiTemplate extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5260450698301464119L;
	private UiTemplateType templateType;
    private Long fKGalleryId;
    private String name;
    private String description;
    private String htmlTemplate;
    private String scriptTemplate;
    
    private Gallery gallery;
    private List<Album> albums = Lists.newArrayList(); // albums
    
    public UiTemplate(){
		super();
	}
    
    public UiTemplate(final UiTemplateType templateType, final String name, final Gallery gallery, final String description, final String htmlTemplate, final String scriptTemplate){
		super();
		this.gallery = gallery;
		this.templateType = templateType;
		this.name = name;
		this.description = description;
		this.htmlTemplate = htmlTemplate;
		this.scriptTemplate = scriptTemplate;
	}
    
    @ManyToOne
    @JoinColumn(name="gallery_id", nullable=false)
    public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}
    
    @JsonProperty(value = "TemplateType")
    @Column(name="template_type", nullable=false, length=50)
    @Enumerated(EnumType.STRING)
    @Field
    public UiTemplateType getTemplateType(){
        return this.templateType;
    }
    
    public void setTemplateType(UiTemplateType templateType){
        this.templateType = templateType;
    }
    
   /* @JsonProperty(value = "FKGalleryId")
    @Column(name="FKGalleryId", nullable=false)
    @Field*/
    @Transient
    public Long getFKGalleryId(){
        return this.fKGalleryId;
    }
    
    public void setFKGalleryId(Long fKGalleryId){
        this.fKGalleryId = fKGalleryId;
    }
    
    @JsonProperty(value = "Name")
    @Column(name="name", nullable=false, length=255)
    @Field
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    @JsonProperty(value = "Description")
    @Column(name="description", nullable=false)
    @Type(type="text")
    @Field
    public String getDescription(){
        return this.description;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    @JsonProperty(value = "HtmlTemplate")
    @Column(name="html_template", nullable=false)
    @Type(type="text")
    @Field
    public String getHtmlTemplate(){
        return this.htmlTemplate;
    }
    
    public void setHtmlTemplate(String htmlTemplate){
        this.htmlTemplate = htmlTemplate;
    }
    
    @JsonProperty(value = "ScriptTemplate")
    @Column(name="script_template", nullable=false)
    @Type(type="text")
    @Field
    public String getScriptTemplate(){
        return this.scriptTemplate;
    }
    
    public void setScriptTemplate(String scriptTemplate){
        this.scriptTemplate = scriptTemplate;
    } 
    
    @ManyToMany(mappedBy = "uiTemplates", fetch=FetchType.LAZY)
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	//
    public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}
	
	public void addAlbums(List<Album> albums) {
		this.albums.addAll(albums);
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UiTemplate pojo = (UiTemplate)o;
        return (new EqualsBuilder()
             .append(templateType, pojo.templateType)
             .append(fKGalleryId, pojo.fKGalleryId)
             .append(name, pojo.name)
             .append(description, pojo.description)
             .append(htmlTemplate, pojo.htmlTemplate)
             .append(scriptTemplate, pojo.scriptTemplate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(templateType)
             .append(fKGalleryId)
             .append(name)
             .append(description)
             .append(htmlTemplate)
             .append(scriptTemplate)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("uiTemplateId").append("='").append(getId()).append("', ");
        sb.append("templateType").append("='").append(getTemplateType()).append("', ");
        sb.append("fKGalleryId").append("='").append(getFKGalleryId()).append("', ");
        sb.append("name").append("='").append(getName()).append("', ");
        sb.append("description").append("='").append(getDescription()).append("', ");
        sb.append("htmlTemplate").append("='").append(getHtmlTemplate()).append("', ");
        sb.append("scriptTemplate").append("='").append(getScriptTemplate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}