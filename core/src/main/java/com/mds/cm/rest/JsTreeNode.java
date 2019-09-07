package com.mds.cm.rest;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.core.exception.ArgumentNullException;

/// <summary>
/// Represents a node that can be JSON-serialized to a form expected by the jsTree jquery widget.
/// </summary>
//[DebuggerDisplay("Text: {Text} ({Nodes.Count} child nodes)")]
@JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
public class JsTreeNode{
	private List<JsTreeNode> nodes;
	private String text;
	private String icon;
	private JsNodeState state;
	private JsNodeAttributes hyperlinkAttributes;
	private JsNodeAttributes listItemAttributes;

	/// <summary>
	/// Gets or sets the text of the node.
	/// </summary>
	/// <value>A String.</value>
	//[JsonProperty(PropertyName = "text", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "text")
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	/// <summary>
	/// Gets or sets the path to an image to use for the node icon, or the class to be applied to the &lt;ins&gt; 
	/// DOM element. When the value contains a slash (/) it is treated as a file and used as a background image.
	/// Any other String will be assigned to the class attribute of the &lt;i&gt; element that is used to
	/// represent the icon.
	/// </summary>
	/// <value>A String.</value>
	//[JsonProperty(PropertyName = "icon", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "icon")
	public String getIcon(){
		return icon;
	}
	
	public void setIcon(String icon){
		this.icon = icon;
	}

	//[JsonProperty(PropertyName = "state", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "state")
	public JsNodeState getState() {
		return state;
	}
	
	public void setState(JsNodeState state) {
		this.state = state;
	}

	/// <summary>
	/// Gets or sets the HTML attributes to be rendered on a node's &lt;a&gt; element.
	/// </summary>
	/// <value>The hyperlink attributes.</value>
	//[JsonProperty(PropertyName = "a_attr", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "a_attr")
	public JsNodeAttributes getHyperlinkAttributes() { 
		return hyperlinkAttributes;
	}
	
	public void setHyperlinkAttributes(JsNodeAttributes hyperlinkAttributes) { 
		this.hyperlinkAttributes = hyperlinkAttributes;
	}

	/// <summary>
	/// Gets or sets the HTML attributes to be rendered on a node's &lt;li&gt; element.
	/// </summary>
	/// <value>The list item attributes.</value>
	//[JsonProperty(PropertyName = "li_attr", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "li_attr")
	public JsNodeAttributes getListItemAttributes() {
		return listItemAttributes;
	}
	
	public void setListItemAttributes(JsNodeAttributes listItemAttributes) {
		this.listItemAttributes = listItemAttributes;
	}

	/// <summary>
	/// Gets the child nodes for this instance. This property is intended for Json.NET serialization.
	/// If a developer wishes to access the nodes, read the remarks carefully to prevent unintended side effects of interacting
	/// with this property. Consider adding a GetNodes() method to retrieve the _nodes variable instead of using this property.
	/// </summary>
	/// <remarks>jsTree requires "children":true to configure a node for lazy loading its children. To accomplish this, we carefully
	/// ensure this property has one of thee values:
	/// null - Indicates this node has no children
	/// Count=0 - Indicates this node has children that should lazy load via ajax.
	/// Count>0 - Indicates this node has children explicitly defined.
	/// This usage works as expected for the 1st & 3rd scenario, but when Count=0, JSON.NET serializes it as "children":[]. 
	/// To fix this, we have a Replace function in <see cref="TreeView.ToJson" /> that converts "children":[] to "children":true.
	/// Various attempts at creating a custom JSON serialize were tried, but they failed primarily due to the recursive
	/// nature of this property. </remarks>
	//[JsonProperty(PropertyName = "children", DefaultValueHandling = DefaultValueHandling.Ignore)]
	@JsonProperty(value = "children")
	private List<JsTreeNode> getNodes()	{
	  return nodes; 
	}

	/// <summary>
	/// Gets or sets a value indicating whether this node has child objects that can be accessed in the data store.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this node has child objects in the data store; otherwise, <c>false</c>.
	/// </value>
	/// <remarks>Read the notes for the <see cref="Nodes" /> property for more details.</remarks>
	//[JsonIgnore]
	@JsonIgnore
	public boolean hasChildren(){
	  return (nodes != null); 
	}
	
	@JsonIgnore
	public void setChildren(){
		if (nodes == null)	{
		  nodes = new ArrayList<JsTreeNode>();
		}
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="JsTreeNode"/> class.
	/// </summary>
	public JsTreeNode()	{
	  state = new JsNodeState();
	  hyperlinkAttributes = new JsNodeAttributes();
	  listItemAttributes = new JsNodeAttributes();
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="JsTreeNode"/> class.
	/// </summary>
	/// <param name="text">The text.</param>
	public JsTreeNode(String text)	{
		this();
		this.text = text;
	}

	/// <summary>
	/// Adds the <paramref name="node" /> to the list of nodes that are children of this instance.
	/// </summary>
	/// <param name="node">The node to add.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="node" /> is null.</exception>
	public void addNode(JsTreeNode node){
	  if (node == null)
		throw new ArgumentNullException("node");

	  if (nodes == null) {
		nodes = new ArrayList<JsTreeNode>();
	  }

	  nodes.add(node);
	}
}