package com.mds.sys.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.common.model.DataEntity;
import com.mds.common.model.JsonDateSerializer;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-11-4
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_calendar")
@Indexed
@XmlRootElement
public class MyCalendar extends DataEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1530209991650812320L;

	/**
     * user
     */
	private User user;

    /**
     * title
     */
    private String title;

    /**
     * details
     */
    private String details;

    /**
     * start date
     */
    private Date startDate;

    /**
     * duration
     */
    private Integer duration;

    /**
     * start time
     */
    private Date startTime;

    /**
     * end time
     */
    private Date endTime;

    private String backgroundColor;

    private String textColor;
    
    public MyCalendar(){
    	super();
    }
    
    public MyCalendar(Date start, Date end){
    	this();
    	
    	this.startTime = start;
    	this.endTime = end;
    }


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
    public User getUser(){
        return this.user;
    }
    
    public void setUser(User user){
        this.user = user;
    }

    @Column(name="title", length=256)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlAttribute
    @JsonProperty(value = "Details")
    @Column(name="details", nullable=true)
    @Type(type="text")
    @Field
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = JsonDateSerializer.class)
    //@DateTimeFormat(iso=ISO.DATE)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Transient
    public Date getEndDate() {
        return DateUtils.addDays(startDate, duration - 1);
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    //@DateTimeFormat(pattern = "HH:mm:ss")
    //@DateTimeFormat(iso=ISO.TIME)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    //@DateTimeFormat(pattern = "HH:mm:ss")
    //@DateTimeFormat(iso=ISO.TIME)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "background_color")
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Column(name = "text_color")
    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
