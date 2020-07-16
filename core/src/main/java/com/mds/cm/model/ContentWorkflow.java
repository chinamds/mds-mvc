// Created using LayerGen 3.5

package com.mds.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.common.model.DataEntity;
import com.mds.wf.model.Workflow;
import com.mds.wf.model.WorkflowDetail;

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
@Table(name="cm_contentworkflow", uniqueConstraints = @UniqueConstraint(columnNames={"contentobject_id", "workflow_id", "workflow_type"}) )
@Indexed
@XmlRootElement
public class ContentWorkflow extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3278677037696281099L;
	private Long contentObjectId;
    private Long workflowId;
    private short workflowType;
    private short status;
    
    private ContentObject contentObject;
    private Workflow workflow;
    private List<ContentActivity> contentActivities = Lists.newArrayList();	// content activities
    
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="contentWorkflow")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
    public List<ContentActivity> getContentActivities() {
		return contentActivities;
	}

	public void setContentActivities(List<ContentActivity> contentActivities) {
		this.contentActivities = contentActivities;
	}
    
    @ManyToOne
    @JoinColumn(name="contentobject_id", nullable=false)
    public ContentObject getContentObject() {
		return contentObject;
	}

	public void setContentObject(ContentObject contentObject) {
		this.contentObject = contentObject;
	}

	@ManyToOne
    @JoinColumn(name="workflow_id", nullable=false)
	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	/*@JsonProperty(value = "EventID")
    @Column(name="EventID", nullable=false)
    @Field*/
	@Transient
    public Long getContentObjectId(){
        return this.contentObjectId;
    }
    
    public void setContentObjectId(Long contentObjectId){
        this.contentObjectId = contentObjectId;
    }
    
    /*@JsonProperty(value = "WorkflowID")
    @Column(name="WorkflowID", nullable=false, length=50)
    @Field*/
    @Transient
    public Long getWorkflowId(){
        return this.workflowId;
    }
    
    public void setWorkflowId(Long workflowId){
        this.workflowId = workflowId;
    }
    
    @JsonProperty(value = "WorkflowType")
    @Column(name="workflow_type", nullable=false)
    @Field
    public short getWorkflowType(){
        return this.workflowType;
    }
    
    public void setWorkflowType(short workflowType){
        this.workflowType = workflowType;
    }
    
    @JsonProperty(value = "Status")
    @Column(name="Status", nullable=false)
    @Field
    public short getStatus(){
        return this.status;
    }
    
    public void setStatus(short status){
        this.status = status;
    }
    
   
    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentWorkflow pojo = (ContentWorkflow)o;
        return (new EqualsBuilder()
             .append(contentObjectId, pojo.contentObjectId)
             .append(workflowId, pojo.workflowId)
             .append(workflowType, pojo.workflowType)
             .append(status, pojo.status)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(contentObjectId)
             .append(workflowId)
             .append(workflowType)
             .append(status)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("id").append("='").append(getId()).append("', ");
        sb.append("contentObjectId").append("='").append(getContentObjectId()).append("', ");
        sb.append("workflowId").append("='").append(getWorkflowId()).append("', ");
        sb.append("workflowType").append("='").append(getWorkflowType()).append("', ");
        sb.append("status").append("='").append(getStatus()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}