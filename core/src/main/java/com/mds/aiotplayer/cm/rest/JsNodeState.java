package com.mds.aiotplayer.cm.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/// <summary>
/// Represents the state of a tree node.
/// </summary>
@JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
public class JsNodeState{
	private boolean expanded;
	private boolean disabled;
	private boolean selected;
	
	////#region Properties

	/// <summary>
	/// Gets or sets whether the node is in an expanded state.
	/// </summary>
	/// <value>A booleanean.</value>
	//[JsonProperty(PropertyName = "opened", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "opened")
	public boolean isExpanded() {
		return expanded; 
	}
	
	public void setExpanded(boolean expanded) {
		this.expanded = expanded; 
	}

	/// <summary>
	/// Gets or sets whether the node is disabled.
	/// </summary>
	/// <value>A booleanean.</value>
	//[JsonProperty(PropertyName = "disabled", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "disabled")
	public boolean isDisabled() { 
		return disabled; 
	}
	
	public void setDisabled(boolean disabled) { 
		this.disabled = disabled; 
	}

	/// <summary>
	/// Gets or sets whether the node is selected.
	/// </summary>
	/// <value>A booleanean.</value>
	//[JsonProperty(PropertyName = "selected", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "selected")
	public boolean isSelected() { 
		return selected; 
	}
	
	public void setSelected(boolean selected) { 
		this.selected = selected; 
	}

	////#endregion
}