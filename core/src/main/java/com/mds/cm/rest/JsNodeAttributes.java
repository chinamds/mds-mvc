package com.mds.cm.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/// <summary>
/// Represents HTML attributes to be rendered on a DOM element corresponding a jsTree treenode in HTML.
/// </summary>
//[DataContract]
@JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
public class JsNodeAttributes{
	////#region Private Fields

	private List<String> cssClasses;
	private String id;
	private String navigationUrl;
	private String toolTip;
	private String dataId;

	////#endregion

	////#region Properties

	/// <summary>
	/// Gets or sets the ID attribute of the DOM element.
	/// </summary>
	/// <value>A String.</value>
	//[JsonProperty(PropertyName = "id", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "id")
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	/// <summary>
	/// Gets or sets the href attribute of the DOM element.
	/// </summary>
	/// <value>A String.</value>
	//[JsonProperty(PropertyName = "href", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "href")
	public String getNavigationUrl(){
		return this.navigationUrl;
	}
	
	public void setNavigationUrl(String navigationUrl){
		this.navigationUrl = navigationUrl;
	}

	/// <summary>
	/// Gets or sets the title attribute of the DOM element.
	/// </summary>
	/// <value>A String.</value>
	//[JsonProperty(PropertyName = "title", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "title")
	public String getToolTip() {
		return this.toolTip;
	}
	
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	/// <summary>
	/// Gets or sets the data-id attribute of the DOM element.
	/// </summary>
	/// <value>A String.</value>
	//[JsonProperty(PropertyName = "data-id", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "data-id")
	public String getDataId() {
		return this.dataId;
	}
	
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/// <summary>
	/// Gets the class attribute of the DOM element. When multiple classes exist, it returns a space-separated String.
	/// </summary>
	/// <value>A String.</value>
	/// <exception cref="NotImplementedException">Thrown when attempting to set this property. Instead, use the 
	/// <see cref="AddCssClass" /> or <see cref="RemoveCssClass" /> methods.</exception>
	//[JsonProperty(PropertyName = "class", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "class")
	public String getCssClasses(){
	  return StringUtils.join(cssClasses, " "); 
	  //private set { throw new NotImplementedException("Setter not implemented for property 'CssClasses' in class MDS.Web.Entity.Attributes. Use method AddCssClass or RemoveCssClass instead."); }
	}

	////#endregion

	////#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="JsNodeAttributes"/> class.
	/// </summary>
	public JsNodeAttributes(){
	  this.cssClasses = new ArrayList<String>();
	}

	////#endregion

	////#region Public Methods

	/// <summary>
	/// Adds the <paramref name="cssClass" /> to the class attribute of the current tree node. If the class
	/// is already specified, no action is taken.
	/// </summary>
	/// <param name="cssClass">The CSS class.</param>
	public void addCssClass(String cssClass){
	  if (!StringUtils.isBlank(cssClass) && !this.cssClasses.contains(cssClass)) {
		  this.cssClasses.add(cssClass);
	  }
	}

	/// <summary>
	/// Removes the <paramref name="cssClass" /> from the class attribute of the current tree node.
	/// </summary>
	/// <param name="cssClass">The CSS class.</param>
	public void removeCssClass(String cssClass)	{
	  if (!StringUtils.isBlank(cssClass) && this.cssClasses.contains(cssClass)) {
		this.cssClasses.remove(cssClass);
	  }
	}

////#endregion
}