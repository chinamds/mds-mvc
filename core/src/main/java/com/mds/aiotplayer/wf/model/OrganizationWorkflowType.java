// Created using LayerGen 3.5

package com.mds.aiotplayer.wf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.core.WorkflowType;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Role;

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
@Table(name="wf_organization_workflowtype", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "workflow_type"}) )
@Indexed
@XmlRootElement
public class OrganizationWorkflowType extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -26851361199973751L;
	/**
	 * 
	 */
	private WorkflowType workflowType;
	private Organization organization;	// organization
	private String description;
    
    private List<Workflow> workflows = Lists.newArrayList();	// workflowType Details
    
    public OrganizationWorkflowType() {
    	super();
    }
    
    public OrganizationWorkflowType(String id) {
    	super();
    	this.id = new Long(id);
    }
        
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="workflowType")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
    public List<Workflow> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(List<Workflow> workflows) {
		this.workflows = workflows;
	}

	@JsonProperty(value = "WorkflowType")
    @Column(name="workflow_type", nullable=false, length=50)
	@Enumerated(EnumType.STRING)
    @Field
    public WorkflowType getWorkflowType(){
        return this.workflowType;
    }
    
    public void setWorkflowType(WorkflowType workflowType){
        this.workflowType = workflowType;
    }
    
    @ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="organization_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	@IndexedEmbedded
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	@Transient
	@Field
	public String getOrganizationCode() {
		if (organization != null && !organization.isRoot())
			return organization.getCode();
		
		return "";
	}
    
	@Column(length = 1024)
	@Field
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganizationWorkflowType pojo = (OrganizationWorkflowType)o;
        return (new EqualsBuilder()
             .append(workflowType, pojo.workflowType)
             .append(organization, pojo.organization)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(workflowType)
             .append(organization)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("workflowType").append("='").append(getWorkflowType()).append("', ");
        sb.append("organization").append("='").append(getOrganization()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}