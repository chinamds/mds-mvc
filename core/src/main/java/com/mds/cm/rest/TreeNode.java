package com.mds.cm.rest;

import org.apache.commons.lang3.NotImplementedException;

import com.mds.util.StringUtils;

/// <summary>
/// Represents a node in a <see cref="TreeView" />.
/// </summary>
public class TreeNode{
	////#region Private Fields

	private TreeView treeView;
	private JsTreeNode node;
	private TreeNodeCollection nodes;

	////#endregion

	////#region Properties

	/// <summary>
	/// Gets or sets the treeview containing the current node.
	/// </summary>
	/// <value>The tree view.</value>
	public TreeView getTreeView()	{
	  return this.treeView;
	}
	
	public void getTreeView(TreeView treeView)	{
	  this.treeView = treeView;
	}

	/// <summary>
	/// Gets or sets the ID of the current node. This value is assigned to the id attribute of the li element generated in the HTML.
	/// </summary>
	/// <value>The ID of the current node.</value>
	public String getId(){
	  return this.node.getListItemAttributes().getId();
	}
	
	public void setId(String id){
	  this.node.getListItemAttributes().setId(id);
	}

	/// <summary>
	/// Gets or sets a piece of data for the current node. This value is assigned to the data-id attribute of the li 
	/// element generated in the HTML. For albums and content objects, specify the <see cref="ContentObjectBo.Id" /> value.
	/// </summary>
	/// <value>A String representing a piece of data for the current node.</value>
	public String getDataId(){
	  return this.node.getListItemAttributes().getDataId();
	}
	
	public void setDataId(String dataId){
	  this.node.getListItemAttributes().setDataId(dataId);
	}

	/// <summary>
	/// Gets or sets the text for the node.
	/// </summary>
	/// <value>The text.</value>
	public String getText()	{
	  return this.node.getText();
	}
	
	public void setText(String text)	{
	  this.node.setText(text);
	}

	/// <summary>
	/// Gets or sets the tool tip for the node. This value is assigned to the title attribute of the hyperlink element
	/// generated in the HTML.
	/// </summary>
	/// <value>The tool tip.</value>
	public String getToolTip(){
	  return this.node.getHyperlinkAttributes().getToolTip();
	}
	
	public void setToolTip(String toolTip){
	  this.node.getHyperlinkAttributes().setToolTip(toolTip);
	}

	/// <summary>
	/// Gets or sets a value indicating whether the current node should display a checkbox.
	/// </summary>
	/// <value><c>true</c> if a check box is to be rendered; otherwise, <c>false</c>.</value>
	public boolean isShowCheckBox(){
	  return (!this.node.getListItemAttributes().getCssClasses().contains("jstree-checkbox-hidden"));
	}
	
	public void setShowCheckBox(boolean showCheckBox){
	  this.node.getListItemAttributes().addCssClass((showCheckBox ? StringUtils.EMPTY : "jstree-checkbox-hidden"));
	}

	/// <summary>
	/// Gets the class attribute of the DOM element. When multiple classes exist, it returns a space-separated String.
	/// </summary>
	/// <value>A String.</value>
	/// <exception cref="NotImplementedException">Thrown when attempting to set this property. Instead, use the 
	/// <see cref="AddCssClass" /> or <see cref="RemoveCssClass" /> methods.</exception>
	public String getCssClasses(){
	  return this.node.getListItemAttributes().getCssClasses();
	}
	
	public void setCssClasses(String cssClasses){
		throw new NotImplementedException("Setter not implemented for property 'CssClasses' in class MDS.Web.Entity.TreeNode. Use method AddCssClass or RemoveCssClass instead.");
	}

	/// <summary>
	/// Gets or sets a value indicating whether this node has child objects that can be accessed in the data store.
	/// When this value is <c>true</c> and the generated HTML does not contain any child nodes, an AJAX callback
	/// is performed to retrieve the nodes. When <c>false</c>, the expand/collapse icon is not rendered and no
	/// callback is performed.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this node has child objects in the data store; otherwise, <c>false</c>.
	/// </value>
	public boolean hasChildren(){
	  return this.node.hasChildren();
	}
	
	public void setChildren(){
	  this.node.setChildren();
	}

	/// <summary>
	/// Gets or sets the base relative or absolute URL to invoke when a tree node is clicked. The album ID of the selected album
	/// is passed to the URL as the query String parameter "aid". Leave this value as null or set to 
	/// an empty String when no navigation is desired. Example: "Gallery.aspx, http://site.com/gallery.aspx"
	/// </summary>
	/// <value>The URL the user is to be sent to when the node is clicked.</value>
	public String getNavigateUrl(){
	  return this.node.getHyperlinkAttributes().getNavigationUrl();
	}
	
	public void setNavigateUrl(String navigationUrl){
	  this.node.getHyperlinkAttributes().setNavigationUrl(navigationUrl);
	}

	/// <summary>
	/// Gets or sets a value indicating whether this <see cref="TreeNode"/> is expanded.
	/// </summary>
	/// <value><c>true</c> if expanded; otherwise, <c>false</c>.</value>
	public boolean isExpanded(){
	  return this.node.getState().isExpanded();
	}
	
	public void setExpanded(boolean expanded){
	  this.node.getState().setExpanded(expanded);
	}

	/// <summary>
	/// Gets or sets a value indicating whether this <see cref="TreeNode"/> is selected.
	/// </summary>
	/// <value><c>true</c> if selected; otherwise, <c>false</c>.</value>
	public boolean isSelected(){
	  return this.node.getState().isSelected();
	}
	
	public void setSelected(boolean selected){
	  this.node.getState().setSelected(selected);
	}

	/// <summary>
	/// Gets or sets a value indicating whether this <see cref="TreeNode"/> is selectable.
	/// </summary>
	/// <value><c>true</c> if selectable; otherwise, <c>false</c>.</value>
	public boolean isSelectable(){
	  return !this.node.getState().isDisabled();
	}
	
	public void setSelectable(boolean selectable){
	  this.node.getState().setDisabled(!selectable);
	}

	/// <summary>
	/// Gets or sets the nodes contained within the current node. The nodes are a hierarchical. Guaranteed to
	/// not be null.
	/// </summary>
	/// <value>The nodes in the treeview.</value>
	public TreeNodeCollection getNodes(){ 
		return nodes;
	}
	
	private void setNodes(TreeNodeCollection nodes){ 
		this.nodes = nodes;
	}

	/// <summary>
	/// Gets a reference to the internal JsTreeNode object. This object is designed to that it can be JSON-serialized
	/// to a form expected by the jsTree jquery widget.
	/// </summary>
	/// <value>The internal JsTreeNode object.</value>
	public JsTreeNode getNodeInternal(){
	  return this.node;
	}

	////#endregion

	////#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="TreeNode"/> class. The <see cref="Nodes" /> property is initialized 
	/// to an empty collection.
	/// </summary>
	public TreeNode(){
	  this.node = new JsTreeNode();
	  this.nodes = new TreeNodeCollection(this);
	}

	////#endregion

	////#region Public Methods

	/// <summary>
	/// Adds the <paramref name="node" /> to the collection of <see cref="JsTreeNode" /> instances.
	/// </summary>
	/// <param name="node">The node to add.</param>
	public void addInternalNode(JsTreeNode node){
	  this.node.addNode(node);
	}

	/// <summary>
	/// Adds the <paramref name="cssClass" /> to the class attribute of the current tree node. If the class
	/// is already specified, no action is taken.
	/// </summary>
	/// <param name="cssClass">The CSS class.</param>
	public void addCssClass(String cssClass){
	  this.node.getListItemAttributes().addCssClass(cssClass);
	}

	/// <summary>
	/// Removes the <paramref name="cssClass" /> from the class attribute of the current tree node.
	/// </summary>
	/// <param name="cssClass">The CSS class.</param>
	public void RemoveCssClass(String cssClass){
	  this.node.getListItemAttributes().removeCssClass(cssClass);
	}

	////#endregion
}