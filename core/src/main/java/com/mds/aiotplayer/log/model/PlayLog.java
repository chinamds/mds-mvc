/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.log.model;

import com.mds.aiotplayer.common.model.IdEntity;
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
@Table(name="log_play")
@Indexed
@XmlRootElement
public class PlayLog extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7520553514289747486L;
	/**
	 * 
	 */
	private String strContent;
    private Byte nContentType;
    private Byte nZone;
    private Float dbDuration;
    private Date dtStartTime;
    private Date dtEndTime;
    private short nLogType;
    private String strUniqueName;
    private String strPlayer;
    private Date dtLogDate;
     
    @JsonProperty(value = "strContent")
    @Column(name="strContent", length=255)
    @Field
    public String getContent(){
        return this.strContent;
    }
    
    public void setContent(String strContent){
        this.strContent = strContent;
    }
    
    @JsonProperty(value = "nContentType")
    @Column(name="nContentType")
    @Type(type="org.hibernate.type.ByteType")
    @FieldBridge(impl = IntegerBridge.class)
    @Field
    public Byte getContentType(){
        return this.nContentType;
    }
    
    public void setContentType(Byte nContentType){
        this.nContentType = nContentType;
    }
    
    @JsonProperty(value = "nZone")
    @Column(name="nZone")
    @Type(type="org.hibernate.type.ByteType")
    @FieldBridge(impl = IntegerBridge.class)
    @Field
    public Byte getZone(){
        return this.nZone;
    }
    
    public void setZone(Byte nZone){
        this.nZone = nZone;
    }
    
    @JsonProperty(value = "dbDuration")
    @Column(name="dbDuration")
    @Field
    public Float getBDuration(){
        return this.dbDuration;
    }
    
    public void setBDuration(Float dbDuration){
        this.dbDuration = dbDuration;
    }
    
    @JsonProperty(value = "dtStartTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtStartTime", nullable=false, length=19)
    @Field
    public Date getStartTime(){
        return this.dtStartTime;
    }
    
    public void setStartTime(Date dtStartTime){
        this.dtStartTime = dtStartTime;
    }
    
    @JsonProperty(value = "dtEndTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtEndTime", nullable=false, length=19)
    @Field
    public Date getEndTime(){
        return this.dtEndTime;
    }
    
    public void setEndTime(Date dtEndTime){
        this.dtEndTime = dtEndTime;
    }
    
    @JsonProperty(value = "nLogType")
    @Column(name="nLogType", nullable=false)
    @Field
    public short getLogType(){
        return this.nLogType;
    }
    
    public void setLogType(short nLogType){
        this.nLogType = nLogType;
    }
    
    @JsonProperty(value = "strUniqueName")
    @Column(name="strUniqueName", nullable=false, length=50)
    @Field
    public String getUniqueName(){
        return this.strUniqueName;
    }
    
    public void setUniqueName(String strUniqueName){
        this.strUniqueName = strUniqueName;
    }
    
    @JsonProperty(value = "strPlayer")
    @Column(name="strPlayer", length=50)
    @Field
    public String getPlayer(){
        return this.strPlayer;
    }
    
    public void setPlayer(String strPlayer){
        this.strPlayer = strPlayer;
    }
    
    @JsonProperty(value = "dtLogDate")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtLogDate", nullable=false, length=19)
    @Field
    public Date getLogDate(){
        return this.dtLogDate;
    }
    
    public void setLogDate(Date dtLogDate){
        this.dtLogDate = dtLogDate;
    }
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayLog pojo = (PlayLog)o;
        return (new EqualsBuilder()
             .append(strContent, pojo.strContent)
             .append(nContentType, pojo.nContentType)
             .append(nZone, pojo.nZone)
             .append(dbDuration, pojo.dbDuration)
             .append(dtStartTime, pojo.dtStartTime)
             .append(dtEndTime, pojo.dtEndTime)
             .append(nLogType, pojo.nLogType)
             .append(strUniqueName, pojo.strUniqueName)
             .append(strPlayer, pojo.strPlayer)
             .append(dtLogDate, pojo.dtLogDate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strContent)
             .append(nContentType)
             .append(nZone)
             .append(dbDuration)
             .append(dtStartTime)
             .append(dtEndTime)
             .append(nLogType)
             .append(strUniqueName)
             .append(strPlayer)
             .append(dtLogDate)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Id").append("='").append(getId()).append("', ");
        sb.append("strContent").append("='").append(getContent()).append("', ");
        sb.append("nContentType").append("='").append(getContentType()).append("', ");
        sb.append("nZone").append("='").append(getZone()).append("', ");
        sb.append("dbDuration").append("='").append(getBDuration()).append("', ");
        sb.append("dtStartTime").append("='").append(getStartTime()).append("', ");
        sb.append("dtEndTime").append("='").append(getEndTime()).append("', ");
        sb.append("nLogType").append("='").append(getLogType()).append("', ");
        sb.append("strUniqueName").append("='").append(getUniqueName()).append("', ");
        sb.append("strPlayer").append("='").append(getPlayer()).append("', ");
        sb.append("dtLogDate").append("='").append(getLogDate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}