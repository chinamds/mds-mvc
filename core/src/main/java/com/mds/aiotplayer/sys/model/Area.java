/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.common.model.TreeEntity;
import com.mds.aiotplayer.common.utils.excel.annotation.ExcelField;

/**
 * Area Entity
 * @author John Lee
 * @version 2013-05-15
 */
@Entity
@Table(name = "sys_area", uniqueConstraints = @UniqueConstraint(columnNames={"parent_id", "code"}))
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "non-lazy")
@Indexed
@XmlRootElement
public class Area extends TreeEntity<Area>  implements Serializable {

	private static final long serialVersionUID = 1L;
	//private Area parent;	// area parent
	//private String code; 	// area code
	//private String name; 	// area name
	private String type; 	// Area Type（1：country/region ；2：Provinces, municipalities directly under the central government；3：地市；4：区县）
	private String remarks;
	
	private List<Organization> organizations = Lists.newArrayList(); // Organizations
	//private List<Area> children = Lists.newArrayList();	// owner child Areas
	//private List<GalleryMapping> galleryMappings = Lists.newArrayList();   // gallery mappings
	
	public Area(){
		super();
		//this.sort = 30;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="parent_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonBackReference
	@ExcelField(title="area.parent", complex=1, align=1, sort=10)
	@Override
	public Area getParent() {
		return parent;
	}

	@Override
	public void setParent(Area parent) {
		this.parent = parent;
	}

	@Column(length = 1)
	@ExcelField(title="area.type", align=1, sort=22)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@ExcelField(title="area.code", align=1, sort=20)
	@Column(length = 100, nullable=false)
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonProperty(value = "Name")
	@ExcelField(title="area.name", align=1, sort=21)
	@Column(length=256)
	@Field
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(length = 1024)
	@ExcelField(title="area.remarks", align=1, sort=23)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "area", fetch=FetchType.LAZY)
	@OrderBy(value="code") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}

	/**
	 * @return the galleryMappings
	 */
	/*@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="area")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<GalleryMapping> getGalleryMappings() {
		return galleryMappings;
	}

	*//**
	 * @param galleryMappings the galleryMappings to set
	 *//*
	public void setGalleryMappings(List<GalleryMapping> galleryMappings) {
		this.galleryMappings = galleryMappings;
	}*/

	@OneToMany(cascade=CascadeType.ALL, mappedBy = "parent", fetch=FetchType.LAZY)
	@OrderBy(value="code") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonManagedReference
	@Override
	public List<Area> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<Area> children) {
		this.children = children;
	}

	/*@Transient
	public static void sortList(List<Area> list, List<Area> sourcelist, Long parentId){
		if (parentId == 0)
		{
			for (int i=0; i<sourcelist.size(); i++){
				Area e = sourcelist.get(i);
				if (e.isTop()){
					list.add(e);
					//enum all children
					for (int j=0; j<sourcelist.size(); j++){
						Area childe = sourcelist.get(j);
						if (childe.isChild(e)){
							sortList(list, sourcelist, e.getId());
							break;
						}
					}
				}
			}
		}else{
			for (int i=0; i<sourcelist.size(); i++){
				Area e = sourcelist.get(i);
				if (e.isChild(parentId)){
					list.add(e);
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Area childe = sourcelist.get(j);
						if (childe.isChild(e)){
							sortList(list, sourcelist, e.getId());
							break;
						}
					}
				}
			}
		}
	}*/
		
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append(this.getParentId())
				.append(this.code)
                .append(this.name)
                .toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		Area src = (Area)source;
		this.name = src.getName(); 	// name
		this.code = src.getCode(); 	// area code
		this.type = src.getType(); 	// Area Type（1：country/region ；2：Provinces, municipalities directly under the central government；3：地市；4：区县）
		this.remarks = src.getRemarks();
		//this.sort = src.getSort();		// sort flag
		this.parent = src.getParent();
	}
		
	/*@Transient
	@JsonIgnore
	public String getFullName(){
		String fullName=this.name;
		Area pArea = this.parent; 
		while (pArea != null){
			if (StringUtils.isBlank(fullName)){
				fullName = pArea.getName();
			}else{
				fullName += " > ";
				fullName += pArea.getName();
			}
		
			pArea = pArea.getParent();
		}
		
		return fullName;
	}*/
}