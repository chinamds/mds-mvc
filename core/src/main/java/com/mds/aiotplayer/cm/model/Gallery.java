// Created using LayerGen 3.5

package com.mds.aiotplayer.cm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.JsonDateSerializer;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.common.model.DataEntity;

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
import org.apache.commons.lang.StringUtils;
import  org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
@Table(name="cm_gallery" )
@Indexed
@XmlRootElement
public class Gallery extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8555312572772037049L;
	private String name;
    private String description;
    private boolean isTemplate;
    
    private List<GallerySetting> gallerySettings = Lists.newArrayList();   // gallery settings
    private List<MimeTypeGallery> mimeTypeGallerys = Lists.newArrayList();   // mimetypes for gallery
    private List<UiTemplate> uiTemplates = Lists.newArrayList();   // templates for gallery
    private List<UserGalleryProfile> userGalleryProfiles = Lists.newArrayList();   // user gallery profiles
    
    private List<GalleryMapping> galleryMappings = Lists.newArrayList();   // gallery mappings
    private List<Album> albums = Lists.newArrayList();	// albums
    
    private List<Role> roles = Lists.newArrayList(); // roles
    private List<DailyList> dailyLists = Lists.newArrayList(); // Tb_ContentLists
    
    public Gallery(){
		super();
	}
    
    public Gallery(String id){
		super();
		this.id = new Long(id);
	}
    
    public Gallery(final String name, final String description, boolean isTemplate, final String currentUser){
		super();
		this.name = name;
		this.description = description;
		this.isTemplate = isTemplate;
		this.currentUser = currentUser;
	}
    
    @JsonProperty(value = "Name")
    @Column(name="name", nullable=false, length=50, unique=true)
    @Field
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    @JsonIgnore
	@Transient
    public String getTitle() throws InvalidGalleryException{
    	GalleryBo gallery = CMUtils.loadGallery(this.id);
        return gallery.getTitle();
    }
     
    @JsonProperty(value = "Description")
    @Column(name="description", nullable=false, length=1000)
    @Field
    public String getDescription(){
        return this.description;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    @JsonProperty(value = "IsTemplate")
    @Column(name="is_template", nullable=false)
    @Field
    public boolean isIsTemplate(){
        return this.isTemplate;
    }
    
    public void setIsTemplate(boolean isTemplate){
        this.isTemplate = isTemplate;
    }   
    
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="gallery")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<GallerySetting> getGallerySettings() {
		return gallerySettings;
	}

	public void setGallerySettings(List<GallerySetting> gallerySettings) {
		this.gallerySettings = gallerySettings;
	}
		
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="gallery")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<MimeTypeGallery> getMimeTypeGallerys() {
		return mimeTypeGallerys;
	}

	public void setMimeTypeGallerys(List<MimeTypeGallery> mimeTypeGallerys) {
		this.mimeTypeGallerys = mimeTypeGallerys;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="gallery")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
    public List<UiTemplate> getUiTemplates() {
		return uiTemplates;
	}

	public void setUiTemplates(List<UiTemplate> uiTemplates) {
		this.uiTemplates = uiTemplates;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="gallery")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<UserGalleryProfile> getUserGalleryProfiles() {
		return userGalleryProfiles;
	}

	public void setUserGalleryProfiles(List<UserGalleryProfile> userGalleryProfiles) {
		this.userGalleryProfiles = userGalleryProfiles;
	}

	/**
	 * @return the galleryMappings
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="gallery")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<GalleryMapping> getGalleryMappings() {
		return galleryMappings;
	}

	/**
	 * @param galleryMappings the galleryMappings to set
	 */
	public void setGalleryMappings(List<GalleryMapping> galleryMappings) {
		this.galleryMappings = galleryMappings;
	}
	
	@Transient
	public String getOrganizations() {
		if (!galleryMappings.isEmpty())
			return StringUtils.join(galleryMappings.stream().filter(g->g.getOrganization() != null).map(g->g.getOrganizationCode()).collect(Collectors.toList()), ',');
		
		return StringUtils.EMPTY;
	}
	
	@Transient
	public Organization getOrganization() {
		if (!galleryMappings.isEmpty())
			return galleryMappings.stream().filter(g->g.getOrganization() != null).map(g->g.getOrganization()).findFirst().orElse(null);
		
		return new Organization(1L);
	}

	/**
	 * @return the albums
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="gallery")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<Album> getAlbums() {
		return albums;
	}

	/**
	 * @param albums the albums to set
	 */
	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}
	
	@ManyToMany(mappedBy = "galleries", fetch=FetchType.LAZY)
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<Role> getRoles() {
		return roles;
	}
		
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="gallery")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<DailyList> getDailyLists() {
		return dailyLists;
	}

	public void setDailyLists(List<DailyList> dailyLists) {
		this.dailyLists = dailyLists;
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gallery pojo = (Gallery)o;
        return (new EqualsBuilder()
             .append(description, pojo.description)
             .append(isTemplate, pojo.isTemplate)
             .append(dateAdded, pojo.dateAdded)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(description)
             .append(isTemplate)
             .append(dateAdded)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("galleryId").append("='").append(getId()).append("', ");
        sb.append("description").append("='").append(getDescription()).append("', ");
        sb.append("isTemplate").append("='").append(isIsTemplate()).append("', ");
        sb.append("dateAdded").append("='").append(getDateAdded()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}