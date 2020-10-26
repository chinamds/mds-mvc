/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
// Created using LayerGen 3.5

package com.mds.aiotplayer.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name="cm_contenttemplate" )
@Indexed
@XmlRootElement
public class ContentTemplate extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1953444634165056338L;
    private String mimeType;
    private String browserId;
    private String htmlTemplate;
    private String scriptTemplate;
    
    public ContentTemplate(){
		super();
	}
    
    public ContentTemplate(final String mimeType, final String browserId, final String htmlTemplate, final String scriptTemplate){
		super();
		this.mimeType = mimeType;
		this.browserId = browserId;
		this.htmlTemplate = htmlTemplate;
		this.scriptTemplate = scriptTemplate;
	}
    
    @JsonProperty(value = "MimeType")
    @Column(name="mime_type", nullable=false, length=200)
    @Field
    public String getMimeType(){
        return this.mimeType;
    }
    
    public void setMimeType(String mimeType){
        this.mimeType = mimeType;
    }
    
    @JsonProperty(value = "BrowserId")
    @Column(name="browser_id", nullable=false, length=50)
    @Field
    public String getBrowserId(){
        return this.browserId;
    }
    
    public void setBrowserId(String browserId){
        this.browserId = browserId;
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
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentTemplate pojo = (ContentTemplate)o;
        return (new EqualsBuilder()
             .append(mimeType, pojo.mimeType)
             .append(browserId, pojo.browserId)
             .append(htmlTemplate, pojo.htmlTemplate)
             .append(scriptTemplate, pojo.scriptTemplate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(mimeType)
             .append(browserId)
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
        sb.append("contentTemplateId").append("='").append(getId()).append("', ");
        sb.append("mimeType").append("='").append(getMimeType()).append("', ");
        sb.append("browserId").append("='").append(getBrowserId()).append("', ");
        sb.append("htmlTemplate").append("='").append(getHtmlTemplate()).append("', ");
        sb.append("scriptTemplate").append("='").append(getScriptTemplate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}