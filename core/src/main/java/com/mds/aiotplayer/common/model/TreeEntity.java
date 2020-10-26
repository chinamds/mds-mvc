/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.Field;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.utils.Reflections;

/**
 * Tree data entity
 * @author John Lee
 * @version 02 January 2017 18:35:45
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class TreeEntity<T> extends DataEntity {

	protected T parent;	// parent object
	protected String code; 	// code
	protected String name; 	// name
	protected String parentCodes;		// parent codes
	
	protected List<T> children = Lists.newArrayList();// children
	
	public TreeEntity() {
		super();
		this.parentCodes = null;
	}
	
	/**
	 * parent object
	 * @return
	 */
	//@JsonBackReference
	@Transient
	@JsonIgnore
	public abstract T getParent();

	/**
	 * parent object
	 * @return
	 */
	public abstract void setParent(T parent);
	
	//@JsonManagedReference
	@Transient
	@JsonIgnore
	public abstract List<T> getChildren();

	public abstract void setChildren(List<T> children);
	
	//@JsonBackReference
	@Transient
	public abstract String getCode();

	public abstract void setCode(String code);
	
	//@JsonBackReference
	@Transient
	public abstract String getName();

	public abstract void setName(String name);

	
	@SuppressWarnings({ "rawtypes" })
	@Transient
	@JsonIgnore
	public String getFullName(){
		String fullName=isRoot() ? "" : this.name;
		TreeEntity parentNode = (TreeEntity)parent; 
		while (parentNode != null && !parentNode.isRoot()){
			if (StringUtils.isBlank(fullName)){
				fullName = parentNode.getName();
			}else{
				fullName = " > " + fullName;
				fullName = parentNode.getName() + fullName;
			}
		
			parentNode = (TreeEntity)parentNode.getParent();
		}
		
		return fullName;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Transient
	@JsonIgnore
	public String getFullCode(){
		String fullCode=isRoot() ? "" : this.code;
		TreeEntity parentNode = (TreeEntity)parent; 
		while (parentNode != null && !parentNode.isRoot()){
			if (StringUtils.isBlank(fullCode)){
				fullCode = parentNode.getCode();
			}else{
				fullCode = " > " + fullCode;
				fullCode = parentNode.getCode() + fullCode;
			}
		
			parentNode = (TreeEntity)parentNode.getParent();
		}
		
		return fullCode;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@JsonIgnore
	@Transient
	public static <T> void sortList(List<T> list, List<T> sourcelist, Long parentId){
		if (parentId == 0)
		{
			for (int i=0; i<sourcelist.size(); i++){
				T e = sourcelist.get(i);
				if (((TreeEntity)e).isTop()){
					list.add(e);
					//enum all children
					for (int j=0; j<sourcelist.size(); j++){
						T childe = sourcelist.get(j);
						if (((TreeEntity)childe).isChild(e)){
							sortList(list, sourcelist, ((TreeEntity)e).getId());
							break;
						}
					}
				}
			}
		}
		else
		{
			for (int i=0; i<sourcelist.size(); i++){
				T e = sourcelist.get(i);
				if (((TreeEntity)e).isChild(parentId)){
					list.add(e);
					// get child node if have
					for (int j=0; j<sourcelist.size(); j++){
						T childe = sourcelist.get(j);
						if (((TreeEntity)childe).isChild(e)){
							sortList(list, sourcelist, ((TreeEntity)e).getId());
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * @return parent Id
	 */
	@Transient
	public Long getParentId() {
		Long id = null;
		if (parent != null){
			id = (Long)Reflections.getFieldValue(parent, "id");
		}
		
		return id;
	}
	
	@Transient
	public long getPId() {
		long id = 0;
		if (parent != null){
			id = ((TreeEntity)parent).getId();
		}
		
		return id;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transient
	@JsonIgnore
	public List<Long> getParentIds() {
		List<Long> ids = Lists.newArrayList();
		T parentNode = this.parent;
		while (parentNode != null){
			ids.add((Long)Reflections.getFieldValue(parentNode, "id"));
			parentNode = (T)((TreeEntity)parentNode).getParent();
		}
		
		return ids;
	}
	
	@Transient
	@JsonIgnore
	public List<Long> getChildIds() {
		List<Long> ids = Lists.newArrayList();
		if (children != null && !children.isEmpty()){
			for (T t : children) {
				ids.add((Long)Reflections.getFieldValue(t, "id"));
				ids.addAll(((TreeEntity)t).getChildIds());
			}
		}
		
		return ids;
	}
	
	@Transient
	@JsonIgnore
	public void setParentCodes(String parentCodes) {
		this.parentCodes = parentCodes;
	}
	
	@Transient
	@JsonIgnore
	public String getParentCodes() {		
		return parentCodes;
	}
	
	/**
	 * @return parent code
	 */
	@Transient
	@JsonIgnore
	public String getParentCode() {
		String pCode = "";
		if (parent != null){
			pCode = (String)Reflections.getFieldValue(parent, "code");
		}
		
		return pCode;
	}
	
	/**
	 * @param parentId the parent Id to set
	 * @return
	 */
	/*public void setParentId(Long parentId){
		this.parentId = parentId;
	}*/
	
	@Transient
	@JsonIgnore
	public static boolean isRoot(Long id){
		return id != null && id.equals(1L);
	}
	
	@JsonIgnore
	@Transient
	public static Long getRootId(){
		return 1L;
	}
		
	@Transient
	@JsonIgnore
	public boolean isRoot(){
		return isRoot(this.id);
	}
	
	@Transient
	@JsonIgnore
	public boolean isTop(){
		return (this.parent != null && ((TreeEntity)this.parent).isRoot());
	}
	
	@Transient
	@JsonIgnore
	public boolean isChild(Long parentId){
		return (this.parent != null && ((TreeEntity)this.parent).getId().equals(parentId));
	}
	
	@Transient
	@JsonIgnore
	public boolean isChild(T parent){
		return (this.parent == parent);
	}
}
