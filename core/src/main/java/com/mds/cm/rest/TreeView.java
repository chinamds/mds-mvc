package com.mds.cm.rest;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/// <summary>
/// Contains functionality for representing a tree. Can be JSON-serialized and then assigned to an instance of a 
/// jQuery jsTree object.
/// </summary>
public class TreeView{
	private TreeNodeCollection nodes;
	private boolean enableCheckBoxPlugin;
	////#region Properties

	/// <summary>
	/// Gets or sets the nodes contained in the treeview. The nodes are a hierarchical, meaning if there are ten nodes
	/// in the treeview but only one at the root level, there will be one item in this collection. To access the remaining
	/// nodes, use the <see cref="TreeNode.Nodes" /> property of each <see cref="TreeNode" /> item. Guaranteed to
	/// not be null.
	/// </summary>
	/// <value>The nodes in the treeview.</value>
	//@JsonProperty(value = "Nodes")
	public TreeNodeCollection getNodes() {
		return nodes;
	}
	
	public void getNodes(TreeNodeCollection nodes) {
		this.nodes.addAll(nodes);
	}

	/// <summary>
	/// Gets or sets a value indicating whether checkbox functionality is desired. The default value
	/// is <c>false</c>. When <c>false</c>, the property <see cref="TreeNode.ShowCheckBox" /> is ignored.
	/// </summary>
	/// <value><c>true</c> if checkbox functionality is desired; otherwise, <c>false</c>.</value>
	//@JsonProperty(value = "EnableCheckBoxPlugin")
	public boolean getEnableCheckBoxPlugin() {
		return enableCheckBoxPlugin;
	}
	
	public void setEnableCheckBoxPlugin(boolean enableCheckBoxPlugin) {
		this.enableCheckBoxPlugin = enableCheckBoxPlugin;
	}

	///// <summary>
	///// Gets a list of the client IDs of the nodes that are to be selected when the treeview is rendered. Guaranteed to
	///// not be null.
	///// </summary>
	//public List<String> NodesToCheckIdArray { get; private set; }

	////#endregion

	////#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="TreeView"/> class. The <see cref="Nodes" /> 
	/// property is initialized to an empty collection.
	/// </summary>
	public TreeView(){
	   this.nodes = new TreeNodeCollection(null);
	  //NodesToCheckIdArray = new List<String>();
	}

	////#endregion

	////#region Public Methods

	/// <summary>
	/// Finds the node in the treeview having the specified data ID. The function searches recursively. Returns null
	/// if no matching item is found.
	/// </summary>
	/// <param name="id">The ID to search for.</param>
	/// <returns>Returns a <see name="TreeNode" /> if a match is found; otherwise returns null.</returns>
	public TreeNode findNodeByDataId(String id)	{
	  return findNodeByDataId(nodes, id);
	}

	/// <summary>
	/// Serializes the current instance to JSON. The resulting String can be used as the data source for the jQuery 
	/// treeview widget.
	/// </summary>
	/// <returns>Returns the current instance as a JSON String.</returns>
	public String toJson()	{
	  List<JsTreeNode> nodes = this.nodes.stream().map(n -> n.getNodeInternal()).collect(Collectors.toList());
	  String json="";
	try {
		json = new ObjectMapper().writeValueAsString(nodes);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

	  // Tweak JSON so that jstree treats nodes with empty children as a lazy-loaded node. See remarks
	  // for JsTreeNode.Nodes for more info.
	  return json.replace("\"children\":[]", "\"children\":true");
	}

	////#endregion

	////#region Private Functions

	/// <summary>
	/// Finds the node in the <paramref name="nodes" /> collection having the specified data ID. The
	/// function searches recursively. Returns null if no matching item is found.
	/// </summary>
	/// <param name="nodes">The nodes to search.</param>
	/// <param name="id">The ID to search for.</param>
	/// <returns>Returns a <see name="TreeNode" /> if a match is found; otherwise returns null.</returns>
	private TreeNode findNodeByDataId(List<TreeNode> nodes, String id){
	  for (TreeNode node : nodes) {
		if (node.getDataId().equals(id)){
		  return node;
		}else{
		  TreeNode matchingNode = findNodeByDataId(node.getNodes(), id);

		  if (matchingNode != null)
			return matchingNode;
		}
	  }

	  return null;
	}

	////#endregion
}

 