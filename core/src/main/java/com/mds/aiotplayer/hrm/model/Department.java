/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.common.model.TreeEntity;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.common.utils.excel.annotation.ExcelField;

/**
 * Department Entity
 * <p>User: John Lee
 * <p>Date: 2017/5/27 20:49
 * <p>Version: 1.0
 */
@Entity
@Table(name = "hrm_department")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class Department extends TreeEntity<Department>  implements Serializable {

	private static final long serialVersionUID = 1L;
	//private String code; 	// department code
	private String lookup;
	private String remarks;
	
	//private List<Department> children = Lists.newArrayList();	// owner child Departments
	private List<StaffDepartment> staffDepartments = Lists.newArrayList();	// staff departments
	private List<GalleryMapping> galleryMappings = Lists.newArrayList();   // gallery mappings
	
	private List<Role> roles = Lists.newArrayList(); // roles
	
	public Department(){
		super();
		//this.sort = 30;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="parent_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonBackReference
	@Override
	public Department getParent() {
		return parent;
	}

	@Override
	public void setParent(Department parent) {
		this.parent = parent;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="department")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<StaffDepartment> getStaffDepartments() {
		return staffDepartments;
	}

	public void setStaffDepartments(List<StaffDepartment> staffDepartments) {
		this.staffDepartments = staffDepartments;
	}

	/**
	 * @return the galleryMappings
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="department")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<GalleryMapping> getGalleryMappings() {
		return galleryMappings;
	}

	/**
	 * @param galleryMappings the galleryMappings to set
	 */
	public void setGalleryMappings(List<GalleryMapping> galleryMappings) {
		this.galleryMappings = galleryMappings;
	}
	
	@ManyToMany(mappedBy = "departments", fetch=FetchType.LAZY)
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<Role> getRoles() {
		return roles;
	}
		
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Column(length = 100)
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonProperty(value = "Name")
	@ExcelField(title="department.name", align=2, sort=21)
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
	
	@Column(length = 20)
	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	@Column(length = 1024)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	

	@OneToMany(cascade=CascadeType.ALL, mappedBy = "parent", fetch=FetchType.LAZY)
	@OrderBy(value="code") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonManagedReference
	@Override
	public List<Department> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<Department> children) {
		this.children = children;
	}

	/*@Transient
	public static void sortList(List<Department> list, List<Department> sourcelist, Long parentId){
		if (parentId == 0)
		{
			for (int i=0; i<sourcelist.size(); i++){
				Department e = sourcelist.get(i);
				if (e.getParent()==null)
				{
					list.add(e);
					//enum all children
					for (int j=0; j<sourcelist.size(); j++){
						Department childe = sourcelist.get(j);
						if (childe.getParent()!=null && childe.getParent().getId()!=null
								&& childe.getParent().getId().equals(e.getId())){
							sortList(list, sourcelist, e.getId());
							break;
						}
					}
				}
			}
		}
		else
		{
			for (int i=0; i<sourcelist.size(); i++){
				Department e = sourcelist.get(i);
				if (e.getParent()!=null && e.getParent().getId()!=null
						&& e.getParent().getId().equals(parentId)){
					list.add(e);
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Department childe = sourcelist.get(j);
						if (childe.getParent()!=null && childe.getParent().getId()!=null
								&& childe.getParent().getId().equals(e.getId())){
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
		return name;
	}
	
	@Override
	public void copyFrom(Object source) {
		Department src = (Department)source;
		this.name = src.getName(); 	// name
		this.code = src.getCode(); 	// department code
		this.lookup = src.getLookup(); 	// 
		this.remarks = src.getRemarks();
		//this.sort = src.getSort();		// sort flag
		this.parent = src.getParent();
	}
}