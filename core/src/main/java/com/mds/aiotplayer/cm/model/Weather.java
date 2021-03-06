/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.cm.model;

import com.mds.aiotplayer.common.model.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.aiotplayer.common.model.JsonDateSerializer;
import com.mds.aiotplayer.sys.model.Organization;

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
@Table(name="cm_weather", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "content_name"}))
@Indexed
@XmlRootElement
public class Weather extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1104872644477589206L;
	private String contentName;
    private String description;
    private int moisture;
    private int temperature;
    private String strTextFontName;
    private int size;
    private String strTextColor;
    private boolean fontItalic;
    private boolean fontBold;
    private String bKImage;
    private boolean bKImageFlag;
    private String bKColor;
    private String typestr;
    private String weatherWarningImage;
    private String city;
    private String country;
    private String monthFormats;
    private String weekFormats;
    private String ttFormats;
    private String arrangementFormats;
    private String timeFormats;
    private String dateFormats;
    private Date dtDayDate;
    
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
     
    @JsonProperty(value = "contentName")
    @Column(name="content_name", nullable=false, length=50)
    @Field
    public String getContentName(){
        return this.contentName;
    }
    
    public void setContentName(String contentName){
        this.contentName = contentName;
    }
    
    @JsonProperty(value = "description")
    @Column(name="description", nullable=false, length=256)
    @Field
    public String getDescription(){
        return this.description;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    @JsonProperty(value = "moisture")
    @Column(name="moisture", nullable=false)
    @Field
    public int getMoisture(){
        return this.moisture;
    }
    
    public void setMoisture(int moisture){
        this.moisture = moisture;
    }
    
    @JsonProperty(value = "temperature")
    @Column(name="temperature", nullable=false)
    @Field
    public int getTemperature(){
        return this.temperature;
    }
    
    public void setTemperature(int temperature){
        this.temperature = temperature;
    }
    
    @JsonProperty(value = "strTextFontName")
    @Column(name="strTextFontName", nullable=false, length=50)
    @Field
    public String getTextFontName(){
        return this.strTextFontName;
    }
    
    public void setTextFontName(String strTextFontName){
        this.strTextFontName = strTextFontName;
    }
    
    @JsonProperty(value = "Size")
    @Column(name="Size", nullable=false)
    @Field
    public int getSize(){
        return this.size;
    }
    
    public void setSize(int size){
        this.size = size;
    }
    
    @JsonProperty(value = "strTextColor")
    @Column(name="strTextColor", nullable=false, length=50)
    @Field
    public String getTextColor(){
        return this.strTextColor;
    }
    
    public void setTextColor(String strTextColor){
        this.strTextColor = strTextColor;
    }
    
    @JsonProperty(value = "FontItalic")
    @Column(name="FontItalic", nullable=false)
    @Field
    public boolean isFontItalic(){
        return this.fontItalic;
    }
    
    public void setFontItalic(boolean fontItalic){
        this.fontItalic = fontItalic;
    }
    
    @JsonProperty(value = "FontBold")
    @Column(name="FontBold", nullable=false)
    @Field
    public boolean isFontBold(){
        return this.fontBold;
    }
    
    public void setFontBold(boolean fontBold){
        this.fontBold = fontBold;
    }
    
    @JsonProperty(value = "bKImage")
    @Column(name="bKImage", nullable=false, length=200)
    @Field
    public String getBKImage(){
        return this.bKImage;
    }
    
    public void setBKImage(String bKImage){
        this.bKImage = bKImage;
    }
    
    @JsonProperty(value = "bKImageFlag")
    @Column(name="bKImageFlag", nullable=false)
    @Field
    public boolean isKImageFlag(){
        return this.bKImageFlag;
    }
    
    public void setKImageFlag(boolean bKImageFlag){
        this.bKImageFlag = bKImageFlag;
    }
    
    @JsonProperty(value = "bKColor")
    @Column(name="bKColor", nullable=false, length=50)
    @Field
    public String getBKColor(){
        return this.bKColor;
    }
    
    public void setBKColor(String bKColor){
        this.bKColor = bKColor;
    }
    
    @JsonProperty(value = "typestr")
    @Column(name="typestr", nullable=false, length=20)
    @Field
    public String getTypestr(){
        return this.typestr;
    }
    
    public void setTypestr(String typestr){
        this.typestr = typestr;
    }
    
    @JsonProperty(value = "weatherWarningImage")
    @Column(name="weatherWarningImage", nullable=false, length=250)
    @Field
    public String getWeatherWarningImage(){
        return this.weatherWarningImage;
    }
    
    public void setWeatherWarningImage(String weatherWarningImage){
        this.weatherWarningImage = weatherWarningImage;
    }
    
    @JsonProperty(value = "city")
    @Column(name="city", nullable=false, length=200)
    @Field
    public String getCity(){
        return this.city;
    }
    
    public void setCity(String city){
        this.city = city;
    }
    
    @JsonProperty(value = "country")
    @Column(name="country", nullable=false, length=200)
    @Field
    public String getCountry(){
        return this.country;
    }
    
    public void setCountry(String country){
        this.country = country;
    }
    
    @JsonProperty(value = "monthFormats")
    @Column(name="monthFormats", nullable=false, length=50)
    @Field
    public String getMonthFormats(){
        return this.monthFormats;
    }
    
    public void setMonthFormats(String monthFormats){
        this.monthFormats = monthFormats;
    }
    
    @JsonProperty(value = "weekFormats")
    @Column(name="weekFormats", nullable=false, length=50)
    @Field
    public String getWeekFormats(){
        return this.weekFormats;
    }
    
    public void setWeekFormats(String weekFormats){
        this.weekFormats = weekFormats;
    }
    
    @JsonProperty(value = "ttFormats")
    @Column(name="ttFormats", nullable=false, length=50)
    @Field
    public String getTtFormats(){
        return this.ttFormats;
    }
    
    public void setTtFormats(String ttFormats){
        this.ttFormats = ttFormats;
    }
    
    @JsonProperty(value = "ArrangementFormats")
    @Column(name="ArrangementFormats", nullable=false, length=50)
    @Field
    public String getArrangementFormats(){
        return this.arrangementFormats;
    }
    
    public void setArrangementFormats(String arrangementFormats){
        this.arrangementFormats = arrangementFormats;
    }
    
    @JsonProperty(value = "TimeFormats")
    @Column(name="TimeFormats", nullable=false, length=50)
    @Field
    public String getTimeFormats(){
        return this.timeFormats;
    }
    
    public void setTimeFormats(String timeFormats){
        this.timeFormats = timeFormats;
    }
    
    @JsonProperty(value = "DateFormats")
    @Column(name="DateFormats", nullable=false, length=50)
    @Field
    public String getDateFormats(){
        return this.dateFormats;
    }
    
    public void setDateFormats(String dateFormats){
        this.dateFormats = dateFormats;
    }
    
    @JsonProperty(value = "dtDayDate")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtDayDate", nullable=false, length=19)
    @Field
    public Date getDayDate(){
        return this.dtDayDate;
    }
    
    public void setDayDate(Date dtDayDate){
        this.dtDayDate = dtDayDate;
    }
    
    
    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather pojo = (Weather)o;
        return (new EqualsBuilder()
             .append(contentName, pojo.contentName)
             .append(description, pojo.description)
             .append(moisture, pojo.moisture)
             .append(temperature, pojo.temperature)
             .append(strTextFontName, pojo.strTextFontName)
             .append(size, pojo.size)
             .append(strTextColor, pojo.strTextColor)
             .append(fontItalic, pojo.fontItalic)
             .append(fontBold, pojo.fontBold)
             .append(bKImage, pojo.bKImage)
             .append(bKImageFlag, pojo.bKImageFlag)
             .append(bKColor, pojo.bKColor)
             .append(typestr, pojo.typestr)
             .append(weatherWarningImage, pojo.weatherWarningImage)
             .append(city, pojo.city)
             .append(country, pojo.country)
             .append(monthFormats, pojo.monthFormats)
             .append(weekFormats, pojo.weekFormats)
             .append(ttFormats, pojo.ttFormats)
             .append(arrangementFormats, pojo.arrangementFormats)
             .append(timeFormats, pojo.timeFormats)
             .append(dateFormats, pojo.dateFormats)
             .append(dtDayDate, pojo.dtDayDate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(contentName)
             .append(description)
             .append(moisture)
             .append(temperature)
             .append(strTextFontName)
             .append(size)
             .append(strTextColor)
             .append(fontItalic)
             .append(fontBold)
             .append(bKImage)
             .append(bKImageFlag)
             .append(bKColor)
             .append(typestr)
             .append(weatherWarningImage)
             .append(city)
             .append(country)
             .append(monthFormats)
             .append(weekFormats)
             .append(ttFormats)
             .append(arrangementFormats)
             .append(timeFormats)
             .append(dateFormats)
             .append(dtDayDate)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Id").append("='").append(getId()).append("', ");
        sb.append("contentName").append("='").append(getContentName()).append("', ");
        sb.append("description").append("='").append(getDescription()).append("', ");
        sb.append("moisture").append("='").append(getMoisture()).append("', ");
        sb.append("temperature").append("='").append(getTemperature()).append("', ");
        sb.append("strTextFontName").append("='").append(getTextFontName()).append("', ");
        sb.append("size").append("='").append(getSize()).append("', ");
        sb.append("strTextColor").append("='").append(getTextColor()).append("', ");
        sb.append("fontItalic").append("='").append(isFontItalic()).append("', ");
        sb.append("fontBold").append("='").append(isFontBold()).append("', ");
        sb.append("bKImage").append("='").append(getBKImage()).append("', ");
        sb.append("bKImageFlag").append("='").append(isKImageFlag()).append("', ");
        sb.append("bKColor").append("='").append(getBKColor()).append("', ");
        sb.append("typestr").append("='").append(getTypestr()).append("', ");
        sb.append("weatherWarningImage").append("='").append(getWeatherWarningImage()).append("', ");
        sb.append("city").append("='").append(getCity()).append("', ");
        sb.append("country").append("='").append(getCountry()).append("', ");
        sb.append("monthFormats").append("='").append(getMonthFormats()).append("', ");
        sb.append("weekFormats").append("='").append(getWeekFormats()).append("', ");
        sb.append("ttFormats").append("='").append(getTtFormats()).append("', ");
        sb.append("arrangementFormats").append("='").append(getArrangementFormats()).append("', ");
        sb.append("timeFormats").append("='").append(getTimeFormats()).append("', ");
        sb.append("dateFormats").append("='").append(getDateFormats()).append("', ");
        sb.append("dtDayDate").append("='").append(getDayDate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}