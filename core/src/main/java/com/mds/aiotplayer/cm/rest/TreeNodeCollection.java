package com.mds.aiotplayer.cm.rest;

import java.util.ArrayList;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="TreeNode" /> instances.
/// </summary>
public class TreeNodeCollection extends ArrayList<TreeNode>{
	private TreeNode parent;
	/// <summary>
	/// Gets or sets a reference to the current collection's parent tree node.
	/// </summary>
	/// <value>The parent.</value>
	private TreeNode getParent() { 
		return parent;
	}
	
	private void setParent(TreeNode parent) { 
		this.parent = parent;
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="TreeNodeCollection"/> class.
	/// </summary>
	private TreeNodeCollection(){
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="TreeNodeCollection"/> class.
	/// </summary>
	public TreeNodeCollection(TreeNode parent){
	  this.parent = parent;
	}

	/// <summary>
	/// Adds the specified tree node.
	/// </summary>
	/// <param name="item">The tree node to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addTreeNode(TreeNode item)	{
	  if (item == null)
		throw new ArgumentNullException("item", "Cannot add null to an existing TreeNodeCollection. Items.Count = " + size());

	  if (this.parent != null)  {
		this.parent.addInternalNode(item.getNodeInternal());
		this.parent.setChildren();
	  }

	  add(item);
	}
}
