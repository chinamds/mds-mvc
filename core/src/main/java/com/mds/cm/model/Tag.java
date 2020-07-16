// Created using LayerGen 3.5

package com.mds.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.common.model.BaseObject;
import com.mds.sys.model.Role;

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
@Table(name="cm_tag" )
@Indexed
@XmlRootElement
public class Tag extends BaseObject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1672821983046262319L;
	private String tagName;
	private List<MetadataTag> meatadataTags = Lists.newArrayList();	// Metadata Tags
 
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="tag")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
    public List<MetadataTag> getMeatadataTags() {
		return meatadataTags;
	}

	public void setMeatadataTags(List<MetadataTag> meatadataTags) {
		this.meatadataTags = meatadataTags;
	}

	@JsonProperty(value = "TagName")
    @Id
    @Column(name="tag_name", nullable=false, length=100)
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @DocumentId
    public String getTagName(){
        return this.tagName;
    }
    
    public void setTagName(String tagName){
        this.tagName = tagName;
    }
    


    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag pojo = (Tag)o;
        return (new EqualsBuilder()
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("tagName").append("='").append(getTagName()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

	@Override
	public void copyFrom(Object source) {
		 this.tagName = ((Tag)source).getTagName();
	}

}