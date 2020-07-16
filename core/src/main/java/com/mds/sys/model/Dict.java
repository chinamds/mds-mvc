/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.mds.sys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.common.model.DataEntity;
import com.mds.common.utils.excel.annotation.ExcelField;

/**
 * dict Entity
 * @author John Lee
 * @version 19/08/2017 13:20:35
 */
@Entity
@Table(name = "sys_dict", uniqueConstraints = @UniqueConstraint(columnNames={"category", "word"}))
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate

public class Dict extends DataEntity{

	private static final long serialVersionUID = 1L;
	private DictCategory category;	//dictionary  category
	private String word;	// word
	private String value;	// default value
	private String description;// description
	private Integer sort;	// sort

	public Dict() {
		super();
		this.category = DictCategory.notspecified;
	}
	
	public Dict(DictCategory category, String word, String value){
		this.category = category;
		this.word = word;
		this.value = value;
	}
	
	@XmlAttribute
    @JsonProperty(value = "Value")
    @Column(name="value", nullable=true)
    @Type(type="text")
    @Field
    @ExcelField(title="dict.value", align=1, sort=22)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlAttribute
	@JsonProperty(value = "Word")
    @Column(name="word", nullable=false, length=512)
	@ExcelField(title="dict.word", align=1, sort=21)
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	@JsonProperty(value = "Category")
    @Column(name="category", nullable=false, length=100)
	@ExcelField(title="dict.category", align=1, sort=20)
	@Enumerated(EnumType.STRING)
	public DictCategory getCategory() {
		return category;
	}

	public void setCategory(DictCategory category) {
		this.category = category;
	}

	@XmlAttribute
	@JsonProperty(value = "Description")
    @Column(name="description", nullable=true, length=1024)
	@ExcelField(title="dict.description", align=1, sort=24)
    @Field
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlAttribute
	@JsonProperty(value = "Sort")
	@ExcelField(title="dict.sort", align=3, sort=23)
    @Field
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public void copyFrom(Object source) {
		Dict src = (Dict)source;
		this.category = src.getCategory(); 	// cataloeu
		this.word = src.getWord(); 	// word
		this.value = src.getValue(); 	// value
		this.description = src.getDescription(); // description
		this.sort = src.getSort();		// sort flag
	}
}