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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.JsonDateSerializer;
import com.mds.aiotplayer.sys.model.Organization;

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
@Table(name="pl_playlist", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "strScheduleName"}))
@Indexed
@XmlRootElement
public class Playlist extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9156452209251824301L;
	private String strScheduleName;
    private String strScheduleDesc;
    private boolean bAutoPlay;
    private boolean bStopAndQuit;
    private boolean bIsTimeSchedule;
    private Date dtStart;
    private Date dtEnd;
    private String strStartTime2;
    private String strEndTime2;
    private String company;
    private short nGroupLoop;
    private short nGroupNumber;
    private short approvalLevel;
    private short approvalStatus;
    
    private List<PlaylistItem> playlistItems = Lists.newArrayList(); // playlist items
    private Organization organization;	// organization
        
    @ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="organization_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
     
    @JsonProperty(value = "strScheduleName")
    @Column(name="strScheduleName", nullable=false, length=50)
    @Field
    public String getScheduleName(){
        return this.strScheduleName;
    }
    
    public void setScheduleName(String strScheduleName){
        this.strScheduleName = strScheduleName;
    }
    
    @JsonProperty(value = "strScheduleDesc")
    @Column(name="strScheduleDesc", nullable=false, length=256)
    @Field
    public String getScheduleDesc(){
        return this.strScheduleDesc;
    }
    
    public void setScheduleDesc(String strScheduleDesc){
        this.strScheduleDesc = strScheduleDesc;
    }
    
    @JsonProperty(value = "bAutoPlay")
    @Column(name="bAutoPlay", nullable=false)
    @Field
    public boolean isAutoPlay(){
        return this.bAutoPlay;
    }
    
    public void setAutoPlay(boolean bAutoPlay){
        this.bAutoPlay = bAutoPlay;
    }
    
    @JsonProperty(value = "bStopAndQuit")
    @Column(name="bStopAndQuit", nullable=false)
    @Field
    public boolean isStopAndQuit(){
        return this.bStopAndQuit;
    }
    
    public void setStopAndQuit(boolean bStopAndQuit){
        this.bStopAndQuit = bStopAndQuit;
    }
    
    @JsonProperty(value = "bIsTimeSchedule")
    @Column(name="bIsTimeSchedule", nullable=false)
    @Field
    public boolean isIsTimeSchedule(){
        return this.bIsTimeSchedule;
    }
    
    public void setIsTimeSchedule(boolean bIsTimeSchedule){
        this.bIsTimeSchedule = bIsTimeSchedule;
    }
    
    @JsonProperty(value = "dtStart")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtStart", nullable=false, length=19)
    @Field
    public Date getStart(){
        return this.dtStart;
    }
    
    public void setStart(Date dtStart){
        this.dtStart = dtStart;
    }
    
    @JsonProperty(value = "dtEnd")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtEnd", nullable=false, length=19)
    @Field
    public Date getEnd(){
        return this.dtEnd;
    }
    
    public void setEnd(Date dtEnd){
        this.dtEnd = dtEnd;
    }
    
    @JsonProperty(value = "strStartTime2")
    @Column(name="strStartTime2", length=19)
    @Field
    public String getStartTime2(){
        return this.strStartTime2;
    }
    
    public void setStartTime2(String strStartTime2){
        this.strStartTime2 = strStartTime2;
    }
    
    @JsonProperty(value = "strEndTime2")
    @Column(name="strEndTime2", length=19)
    @Field
    public String getEndTime2(){
        return this.strEndTime2;
    }
    
    public void setEndTime2(String strEndTime2){
        this.strEndTime2 = strEndTime2;
    }
    
    @JsonProperty(value = "company")
    @Column(name="company", length=20)
    @Field
    public String getCompany(){
        return this.company;
    }
    
    public void setCompany(String company){
        this.company = company;
    }
    
    @JsonProperty(value = "nGroupLoop")
    @Column(name="nGroupLoop", nullable=false)
    @Field
    public short getGroupLoop(){
        return this.nGroupLoop;
    }
    
    public void setGroupLoop(short nGroupLoop){
        this.nGroupLoop = nGroupLoop;
    }
    
    @JsonProperty(value = "nGroupNumber")
    @Column(name="nGroupNumber", nullable=false)
    @Field
    public short getGroupNumber(){
        return this.nGroupNumber;
    }
    
    public void setGroupNumber(short nGroupNumber){
        this.nGroupNumber = nGroupNumber;
    }
    
    @JsonProperty(value = "ApprovalLevel")
    @Column(name="ApprovalLevel", nullable=false)
    @Field
    public short getApprovalLevel(){
        return this.approvalLevel;
    }
    
    public void setApprovalLevel(short approvalLevel){
        this.approvalLevel = approvalLevel;
    }
    
    @JsonProperty(value = "ApprovalStatus")
    @Column(name="ApprovalStatus", nullable=false)
    @Field
    public short getApprovalStatus(){
        return this.approvalStatus;
    }
    
    public void setApprovalStatus(short approvalStatus){
        this.approvalStatus = approvalStatus;
    }
    


    /**
	 * @return the playlistItems
	 */
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="playlist")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<PlaylistItem> getPlaylistItems() {
		return playlistItems;
	}

	/**
	 * @param playlistItems the playlistItems to set
	 */
	public void setPlaylistItems(List<PlaylistItem> playlistItems) {
		this.playlistItems = playlistItems;
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Playlist pojo = (Playlist)o;
        return (new EqualsBuilder()
             .append(strScheduleName, pojo.strScheduleName)
             .append(strScheduleDesc, pojo.strScheduleDesc)
             .append(bAutoPlay, pojo.bAutoPlay)
             .append(bStopAndQuit, pojo.bStopAndQuit)
             .append(bIsTimeSchedule, pojo.bIsTimeSchedule)
             .append(dtStart, pojo.dtStart)
             .append(dtEnd, pojo.dtEnd)
             .append(strStartTime2, pojo.strStartTime2)
             .append(strEndTime2, pojo.strEndTime2)
             .append(company, pojo.company)
             .append(nGroupLoop, pojo.nGroupLoop)
             .append(nGroupNumber, pojo.nGroupNumber)
             .append(approvalLevel, pojo.approvalLevel)
             .append(approvalStatus, pojo.approvalStatus)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strScheduleName)
             .append(strScheduleDesc)
             .append(bAutoPlay)
             .append(bStopAndQuit)
             .append(bIsTimeSchedule)
             .append(dtStart)
             .append(dtEnd)
             .append(strStartTime2)
             .append(strEndTime2)
             .append(company)
             .append(nGroupLoop)
             .append(nGroupNumber)
             .append(approvalLevel)
             .append(approvalStatus)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("id").append("='").append(getId()).append("', ");
        sb.append("strScheduleName").append("='").append(getScheduleName()).append("', ");
        sb.append("strScheduleDesc").append("='").append(getScheduleDesc()).append("', ");
        sb.append("bAutoPlay").append("='").append(isAutoPlay()).append("', ");
        sb.append("bStopAndQuit").append("='").append(isStopAndQuit()).append("', ");
        sb.append("bIsTimeSchedule").append("='").append(isIsTimeSchedule()).append("', ");
        sb.append("dtStart").append("='").append(getStart()).append("', ");
        sb.append("dtEnd").append("='").append(getEnd()).append("', ");
        sb.append("strStartTime2").append("='").append(getStartTime2()).append("', ");
        sb.append("strEndTime2").append("='").append(getEndTime2()).append("', ");
        sb.append("company").append("='").append(getCompany()).append("', ");
        sb.append("nGroupLoop").append("='").append(getGroupLoop()).append("', ");
        sb.append("nGroupNumber").append("='").append(getGroupNumber()).append("', ");
        sb.append("approvalLevel").append("='").append(getApprovalLevel()).append("', ");
        sb.append("approvalStatus").append("='").append(getApprovalStatus()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}