// Created using LayerGen 3.5

package com.mds.aiotplayer.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.JsonDateSerializer;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.Orientation;
import com.mds.aiotplayer.sys.model.User;
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
import java.util.Optional;
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
import javax.persistence.Lob;
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
@Table(name="cm_contentobject") //, uniqueConstraints = @UniqueConstraint(columnNames={"album_id", "content"})
@Indexed
@XmlRootElement
public class ContentObject extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1974176291416910624L;
	private String content;
    private String thumbnailFilename;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private long thumbnailSizeKB;
    private String optimizedFilename;
    private int optimizedWidth;
    private int optimizedHeight;
    private long optimizedSizeKB;
    private String originalFilename;
    private int originalWidth;
    private int originalHeight;
    private long originalSizeKB;
    private String externalHtmlSource;
    private String externalType;
    private int seq;
    private boolean isPrivate;
    private ApprovalStatus approvalStatus;
    
    private Album album;
    private List<Metadata> metadatas = Lists.newArrayList();   // meta datas
    private List<ContentQueue> contentQueues = Lists.newArrayList();   // content Queues
    private List<ContentWorkflow> contentWorkflows = Lists.newArrayList();   // content workflows
    private List<DailyListZone> dailyListZones = Lists.newArrayList();   // content in daily list
    
    @ManyToOne
    @JoinColumn(name="album_id", nullable=false)
    public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy="contentObject")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<Metadata> getMetadatas() {
		return metadatas;
	}
	
	public void setMetadatas(List<Metadata> metadatas) {
		this.metadatas = metadatas;
	}
    
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="contentObject")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
    public List<ContentQueue> getContentQueues() {
		return contentQueues;
	}

	public void setContentQueues(List<ContentQueue> contentQueues) {
		this.contentQueues = contentQueues;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="contentObject")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<ContentWorkflow> getContentWorkflows() {
		return contentWorkflows;
	}

	public void setContentWorkflows(List<ContentWorkflow> contentWorkflows) {
		this.contentWorkflows = contentWorkflows;
	}
    
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="contentObject")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
    public List<DailyListZone> getDailyListZones() {
		return dailyListZones;
	}

	public void setDailyListZones(List<DailyListZone> dailyListZones) {
		this.dailyListZones = dailyListZones;
	}

	@JsonProperty(value = "ThumbnailFilename")
    @Column(name="thumbnail_filename", nullable=false, length=255)
    @Field
    public String getThumbnailFilename(){
        return this.thumbnailFilename;
    }
    
    public void setThumbnailFilename(String thumbnailFilename){
        this.thumbnailFilename = thumbnailFilename;
    }
    
    @JsonProperty(value = "ThumbnailWidth")
    @Column(name="thumbnail_width", nullable=false)
    @Field
    public int getThumbnailWidth(){
        return this.thumbnailWidth;
    }
    
    public void setThumbnailWidth(int thumbnailWidth){
        this.thumbnailWidth = thumbnailWidth;
    }
    
    @JsonProperty(value = "ThumbnailHeight")
    @Column(name="thumbnail_height", nullable=false)
    @Field
    public int getThumbnailHeight(){
        return this.thumbnailHeight;
    }
    
    public void setThumbnailHeight(int thumbnailHeight){
        this.thumbnailHeight = thumbnailHeight;
    }
    
    @JsonProperty(value = "ThumbnailSizeKB")
    @Column(name="thumbnail_size_kb", nullable=false)
    @Field
    public long getThumbnailSizeKB(){
        return this.thumbnailSizeKB;
    }
    
    public void setThumbnailSizeKB(long thumbnailSizeKB){
        this.thumbnailSizeKB = thumbnailSizeKB;
    }
    
    @JsonProperty(value = "OptimizedFilename")
    @Column(name="optimized_filename", nullable=false, length=255)
    @Field
    public String getOptimizedFilename(){
        return this.optimizedFilename;
    }
    
    public void setOptimizedFilename(String optimizedFilename){
        this.optimizedFilename = optimizedFilename;
    }
    
    @JsonProperty(value = "OptimizedWidth")
    @Column(name="optimized_width", nullable=false)
    @Field
    public int getOptimizedWidth(){
        return this.optimizedWidth;
    }
    
    public void setOptimizedWidth(int optimizedWidth){
        this.optimizedWidth = optimizedWidth;
    }
    
    @JsonProperty(value = "OptimizedHeight")
    @Column(name="optimized_height", nullable=false)
    @Field
    public int getOptimizedHeight(){
        return this.optimizedHeight;
    }
    
    public void setOptimizedHeight(int optimizedHeight){
        this.optimizedHeight = optimizedHeight;
    }
    
    @JsonProperty(value = "OptimizedSizeKB")
    @Column(name="optimized_size_kb", nullable=false)
    @Field
    public long getOptimizedSizeKB(){
        return this.optimizedSizeKB;
    }
    
    public void setOptimizedSizeKB(long optimizedSizeKB){
        this.optimizedSizeKB = optimizedSizeKB;
    }
    
    /**
	 * @return the content
	 */
    @Column(name="content", nullable=false, length=1024)
    @Field
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

    
    @JsonProperty(value = "OriginalFilename")
    @Column(name="original_filename", nullable=false, length=1024)
    @Field
    public String getOriginalFilename(){
        return this.originalFilename;
    }
    
	public void setOriginalFilename(String originalFilename){
        this.originalFilename = originalFilename;
    }
    
    @JsonProperty(value = "OriginalWidth")
    @Column(name="original_width", nullable=false)
    @Field
    public int getOriginalWidth(){
        return this.originalWidth;
    }
    
    public void setOriginalWidth(int originalWidth){
        this.originalWidth = originalWidth;
    }
    
    @JsonProperty(value = "OriginalHeight")
    @Column(name="original_height", nullable=false)
    @Field
    public int getOriginalHeight(){
        return this.originalHeight;
    }
    
    public void setOriginalHeight(int originalHeight){
        this.originalHeight = originalHeight;
    }
    
    @JsonProperty(value = "OriginalSizeKB")
    @Column(name="original_size_kb", nullable=false)
    @Field
    public long getOriginalSizeKB(){
        return this.originalSizeKB;
    }
    
    public void setOriginalSizeKB(long originalSizeKB){
        this.originalSizeKB = originalSizeKB;
    }
    
    @JsonProperty(value = "ExternalHtmlSource")
    @Column(name="external_html_source", nullable=false)
    @Type(type="text")
    @Field
    public String getExternalHtmlSource(){
        return this.externalHtmlSource;
    }
    
    public void setExternalHtmlSource(String externalHtmlSource){
        this.externalHtmlSource = externalHtmlSource;
    }
    
    @JsonProperty(value = "ExternalType")
    @Column(name="external_type", nullable=false, length=15)
    @Field
    public String getExternalType(){
        return this.externalType;
    }
    
    public void setExternalType(String externalType){
        this.externalType = externalType;
    }
    
    @JsonProperty(value = "Seq")
    @Column(name="seq", nullable=false)
    @Field
    public int getSeq(){
        return this.seq;
    }
    
    public void setSeq(int seq){
        this.seq = seq;
    }
    
    @JsonProperty(value = "IsPrivate")
    @Column(name="is_private", nullable=false)
    @Field
    public boolean isIsPrivate(){
        return this.isPrivate;
    }
    
    public void setIsPrivate(boolean isPrivate){
        this.isPrivate = isPrivate;
    }
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentObject pojo = (ContentObject)o;
        return (new EqualsBuilder()
             .append(thumbnailFilename, pojo.thumbnailFilename)
             .append(thumbnailWidth, pojo.thumbnailWidth)
             .append(thumbnailHeight, pojo.thumbnailHeight)
             .append(thumbnailSizeKB, pojo.thumbnailSizeKB)
             .append(optimizedFilename, pojo.optimizedFilename)
             .append(optimizedWidth, pojo.optimizedWidth)
             .append(optimizedHeight, pojo.optimizedHeight)
             .append(optimizedSizeKB, pojo.optimizedSizeKB)
             .append(originalFilename, pojo.originalFilename)
             .append(originalWidth, pojo.originalWidth)
             .append(originalHeight, pojo.originalHeight)
             .append(originalSizeKB, pojo.originalSizeKB)
             .append(externalHtmlSource, pojo.externalHtmlSource)
             .append(externalType, pojo.externalType)
             .append(seq, pojo.seq)
             .append(createdBy, pojo.createdBy)
             .append(dateAdded, pojo.dateAdded)
             .append(lastModifiedBy, pojo.lastModifiedBy)
             .append(dateLastModified, pojo.dateLastModified)
             .append(isPrivate, pojo.isPrivate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(thumbnailFilename)
             .append(thumbnailWidth)
             .append(thumbnailHeight)
             .append(thumbnailSizeKB)
             .append(optimizedFilename)
             .append(optimizedWidth)
             .append(optimizedHeight)
             .append(optimizedSizeKB)
             .append(originalFilename)
             .append(originalWidth)
             .append(originalHeight)
             .append(originalSizeKB)
             .append(externalHtmlSource)
             .append(externalType)
             .append(seq)
             .append(createdBy)
             .append(dateAdded)
             .append(lastModifiedBy)
             .append(dateLastModified)
             .append(isPrivate)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("contentObjectId").append("='").append(getId()).append("', ");
        sb.append("thumbnailFilename").append("='").append(getThumbnailFilename()).append("', ");
        sb.append("thumbnailWidth").append("='").append(getThumbnailWidth()).append("', ");
        sb.append("thumbnailHeight").append("='").append(getThumbnailHeight()).append("', ");
        sb.append("thumbnailSizeKB").append("='").append(getThumbnailSizeKB()).append("', ");
        sb.append("optimizedFilename").append("='").append(getOptimizedFilename()).append("', ");
        sb.append("optimizedWidth").append("='").append(getOptimizedWidth()).append("', ");
        sb.append("optimizedHeight").append("='").append(getOptimizedHeight()).append("', ");
        sb.append("optimizedSizeKB").append("='").append(getOptimizedSizeKB()).append("', ");
        sb.append("originalFilename").append("='").append(getOriginalFilename()).append("', ");
        sb.append("originalWidth").append("='").append(getOriginalWidth()).append("', ");
        sb.append("originalHeight").append("='").append(getOriginalHeight()).append("', ");
        sb.append("originalSizeKB").append("='").append(getOriginalSizeKB()).append("', ");
        sb.append("externalHtmlSource").append("='").append(getExternalHtmlSource()).append("', ");
        sb.append("externalType").append("='").append(getExternalType()).append("', ");
        sb.append("seq").append("='").append(getSeq()).append("', ");
        sb.append("createdBy").append("='").append(getCreatedBy()).append("', ");
        sb.append("dateAdded").append("='").append(getDateAdded()).append("', ");
        sb.append("lastModifiedBy").append("='").append(getLastModifiedBy()).append("', ");
        sb.append("dateLastModified").append("='").append(getDateLastModified()).append("', ");
        sb.append("isPrivate").append("='").append(isIsPrivate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

   /// <summary>
   		/// Calculates the actual rotation amount that must be applied based on the user's requested rotation 
   		/// and the file's actual orientation.
   		/// </summary>
   		/// <returns>An instance of <see cref="ContentObjectRotation" />.</returns>
   		public ContentObjectRotation CalculateNeededRotation(ContentObjectRotation Rotation)
   		{
   			Orientation fileRotation = GetOrientation(); // Actual rotation of the original file, as discovered via orientation metadata
   			ContentObjectRotation userRotation = Rotation; // Desired rotation by the user

   			if (userRotation == ContentObjectRotation.NotSpecified)
   			{
   				userRotation = ContentObjectRotation.Rotate0;
   			}

   			switch (fileRotation)
   			{
   				case None:
   				case Normal:
   					return userRotation;

   				case Rotated90:
   					switch (userRotation)
   					{
   						case Rotate0: return ContentObjectRotation.Rotate270;
   						case Rotate90: return ContentObjectRotation.Rotate0;
   						case Rotate180: return ContentObjectRotation.Rotate90;
   						case Rotate270: return ContentObjectRotation.Rotate180;
   					}
   					break;

   				case Rotated180:
   					switch (userRotation)
   					{
   						case Rotate0: return ContentObjectRotation.Rotate180;
   						case Rotate90: return ContentObjectRotation.Rotate270;
   						case Rotate180: return ContentObjectRotation.Rotate0;
   						case Rotate270: return ContentObjectRotation.Rotate90;
   					}
   					break;

   				case Rotated270:
   					switch (userRotation)
   					{
   						case Rotate0: return ContentObjectRotation.Rotate90;
   						case Rotate90: return ContentObjectRotation.Rotate180;
   						case Rotate180: return ContentObjectRotation.Rotate270;
   						case Rotate270: return ContentObjectRotation.Rotate0;
   					}
   					break;
   			}

   			return ContentObjectRotation.NotSpecified;
   		}
   		
   	/// <summary>
	/// Gets the orientation of the original media file. The value is retrieved from the metadata value for 
	/// <see cref="MetadataItemName.Orientation" />. Returns <see cref="Orientation.None" /> if no orientation 
	/// metadata is found, which will be the case for any media file not having orientation metadata embedded
	/// in the media file.
	/// </summary>
	/// <returns>An instance of <see cref="Orientation" />.</returns>
	public Orientation GetOrientation()	{
		Optional<Metadata> orientationMeta = metadatas.stream().filter(m->m.getMetaName() == MetadataItemName.Orientation).findFirst();
		if (orientationMeta.isPresent())
		{
			if (StringUtils.isNumeric(orientationMeta.get().getRawValue()))
			{
				//int orientationRaw = Integer.parseInt(orientationMeta.get().getRawValue());
				Orientation orientation = Orientation.valueOf(orientationMeta.get().getRawValue());
				switch (orientation)
				{
					case Rotated90:
					case Rotated180:
					case Rotated270:
						return orientation;
				}
			}
		}

		return Orientation.None;
	}

	@JsonProperty(value = "ApprovalStatus")
    @Column(name="approval_status", nullable=false, length=50)
	@Enumerated(EnumType.STRING)
    @Field
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
}