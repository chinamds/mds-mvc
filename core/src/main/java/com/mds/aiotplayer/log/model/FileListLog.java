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
@Table(name="log_filelist")
@Indexed
@XmlRootElement
public class FileListLog extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1313224711848813077L;
	private String strUniqueName;
    private String strPlayer;
    private String strTask;
    private String strLog;
    private Date dtLogDate;
     
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
    
    @JsonProperty(value = "strTask")
    @Column(name="strTask", nullable=false, length=50)
    @Field
    public String getTask(){
        return this.strTask;
    }
    
    public void setTask(String strTask){
        this.strTask = strTask;
    }
    
    @JsonProperty(value = "strLog")
    @Column(name="strLog")
    @Type(type="text")
    @Field
    public String getLog(){
        return this.strLog;
    }
    
    public void setLog(String strLog){
        this.strLog = strLog;
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

        FileListLog pojo = (FileListLog)o;
        return (new EqualsBuilder()
             .append(strUniqueName, pojo.strUniqueName)
             .append(strPlayer, pojo.strPlayer)
             .append(strTask, pojo.strTask)
             .append(strLog, pojo.strLog)
             .append(dtLogDate, pojo.dtLogDate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strUniqueName)
             .append(strPlayer)
             .append(strTask)
             .append(strLog)
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
        sb.append("strUniqueName").append("='").append(getUniqueName()).append("', ");
        sb.append("strPlayer").append("='").append(getPlayer()).append("', ");
        sb.append("strTask").append("='").append(getTask()).append("', ");
        sb.append("strLog").append("='").append(getLog()).append("', ");
        sb.append("dtLogDate").append("='").append(getLogDate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}