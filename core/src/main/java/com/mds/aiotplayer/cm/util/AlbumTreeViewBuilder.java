/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.rest.TreeNode;
import com.mds.aiotplayer.cm.rest.TreeView;
import com.mds.aiotplayer.cm.rest.TreeViewOptions;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.SecurityActionsOption;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;

/// <summary>
/// Contains functionality for creating <see cref="TreeView" /> instances.
/// </summary>
public class AlbumTreeViewBuilder{
	//#region Fields

	private TreeViewOptions tvOptions;
	private TreeView tv;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumTreeViewBuilder"/> class.
	/// </summary>
	/// <param name="tvOptions">The treeview options.</param>
	private AlbumTreeViewBuilder(TreeViewOptions tvOptions)	{
	  this.tvOptions = tvOptions;
	  this.tv = new TreeView();
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Generates a <see cref="TreeView" /> instance corresponding to the settings specified in <paramref name="tvOptions" />.
	/// </summary>
	/// <param name="tvOptions">The treeview options.</param>
	/// <returns>An instance of <see cref="TreeView" />.</returns>
	public static TreeView getAlbumsAsTreeView(TreeViewOptions tvOptions) throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
	  AlbumTreeViewBuilder tvBuilder = new AlbumTreeViewBuilder(tvOptions);
	  
	  return tvBuilder.generate();
	}

	//#endregion

	//#region Private Functions

	/// <summary>
	/// Render the treeview with the first two levels of albums that are viewable to the logged on user.
	/// </summary>
	private TreeView generate() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
	  this.tv = new TreeView();
	  tv.setEnableCheckBoxPlugin(this.tvOptions.EnableCheckboxPlugin);
	  List<AlbumBo> rootAlbums = getRootAlbums(); 
	  for (AlbumBo rootAlbum : rootAlbums)  {
		// Add root node.
		TreeNode rootNode = new TreeNode();

		String albumTitle = getRootAlbumTitle(rootAlbum);
		rootNode.setText(albumTitle);
		rootNode.setToolTip(albumTitle);
		rootNode.setId(StringUtils.join("tv_", Long.toString(rootAlbum.getId())));
		rootNode.setDataId(Long.toString(rootAlbum.getId()));
		rootNode.setExpanded(true);
		rootNode.addCssClass("jstree-root-node");

		if (!StringUtils.isBlank(this.tvOptions.NavigateUrl)){
		  String url = rootAlbum.getIsVirtualAlbum() ? this.tvOptions.NavigateUrl 
				  : HelperFunctions.addQueryStringParameter(this.tvOptions.NavigateUrl, StringUtils.join("aid=", Long.toString(rootAlbum.getId())));
		  rootNode.setNavigateUrl(url);
		}

		if (this.tvOptions.EnableCheckboxPlugin){
		  rootNode.setShowCheckBox(!rootAlbum.getIsVirtualAlbum() 
				  && UserUtils.isUserAuthorized(SecurityActions.getSecurityAction(this.tvOptions.RequiredSecurityPermissions), RoleUtils.getMDSRolesForUser(), rootAlbum.getId(), rootAlbum.getGalleryId(), rootAlbum.getIsPrivate(), SecurityActionsOption.RequireOne, rootAlbum.getIsVirtualAlbum()));
		  rootNode.setSelectable(rootNode.isShowCheckBox());
		}else{
		  rootNode.setSelectable(true);
		}
		//if (!rootNode.Selectable) rootNode.HoverCssClass = StringUtils.EMPTY;

		// Select and check this node if needed.
		if (this.tvOptions.SelectedAlbumIds.contains(rootAlbum.getId())){
		  rootNode.setSelected(true);
		}

		this.tv.getNodes().addTreeNode(rootNode);

		// Add the first level of albums below the root album.
		bindAlbumToTreeview(rootAlbum.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList(), rootNode, false);

		// Only display the root node if it is selectable or we added any children to it; otherwise, remove it.
		if (!rootNode.isSelectable() && rootNode.getNodes().size() == 0){
		  this.tv.getNodes().remove(rootNode);
		}
	  }

	  // Make sure all specified albums are visible and checked.
	  for (long albumId : this.tvOptions.SelectedAlbumIds)  {
		AlbumBo album = AlbumUtils.loadAlbumInstance(albumId, false);
		if (UserUtils.isUserAuthorized(SecurityActions.getSecurityAction(this.tvOptions.RequiredSecurityPermissions), RoleUtils.getMDSRolesForUser(), album.getId(), 
				album.getGalleryId(), album.getIsPrivate(), SecurityActionsOption.RequireOne, album.getIsVirtualAlbum()))	{
		  bindSpecificAlbumToTreeview(album);
		}
	  }

	  return this.tv;
	}

	/// <summary>
	/// Add the collection of albums to the specified treeview node.
	/// </summary>
	/// <param name="albums">The collection of albums to add the the treeview node.</param>
	/// <param name="parentNode">The treeview node that will receive child nodes representing the specified albums.</param>
	/// <param name="expandNode">Specifies whether the nodes should be expanded.</param>
	private void bindAlbumToTreeview(List<ContentObjectBo> albums, TreeNode parentNode, boolean expandNode) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException	{
	  for (ContentObjectBo contentObject : albums)  {
		  AlbumBo album = (AlbumBo)contentObject;
		  TreeNode node = new TreeNode();
		  String albumTitle = HelperFunctions.removeHtmlTags(album.getTitle());
		  node.setText(albumTitle);
		  node.setToolTip(albumTitle);
		  node.setId(StringUtils.join("tv_", Long.toString(album.getId())));
		  node.setDataId(Long.toString(album.getId()));
		  node.setExpanded(expandNode);

		if (!StringUtils.isBlank(this.tvOptions.NavigateUrl)){
		  node.setNavigateUrl(HelperFunctions.addQueryStringParameter(this.tvOptions.NavigateUrl
				  , StringUtils.join("aid=", Long.toString(album.getId()))));
		}

		if (this.tvOptions.EnableCheckboxPlugin && !parentNode.isShowCheckBox()){
		  node.setShowCheckBox(!album.getIsVirtualAlbum() 
				  && UserUtils.isUserAuthorized(SecurityActions.getSecurityAction(this.tvOptions.RequiredSecurityPermissions), RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), album.getIsPrivate(), SecurityActionsOption.RequireOne, album.getIsVirtualAlbum()));
		  node.setSelectable(node.isShowCheckBox());
		}else{
		  node.setSelectable(true);
		}

		if (album.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !UserUtils.isAuthenticated()).count()> 0){
		  node.setChildren();
		}

		// Select and check this node if needed.
		if (this.tvOptions.SelectedAlbumIds.contains(album.getId())){
		  node.setExpanded(true);
		  node.setSelected(true);
		  // Expand the child of the selected album.
		  bindAlbumToTreeview(album.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList(), node, false);
		}

		parentNode.getNodes().addTreeNode(node);
	  }
	}

	/// <summary>
	/// Bind the specified album to the treeview. This method assumes the treeview has at least the root node already
	/// built. The specified album can be at any level in the hierarchy. Nodes between the album and the existing top node
	/// are automatically created so that the full node path to the album is shown.
	/// </summary>
	/// <param name="album">An album to be added to the treeview.</param>
	private void bindSpecificAlbumToTreeview(AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException	{
	  if (this.tv.findNodeByDataId(Long.toString(album.getId())) == null)  {
		// Get a stack of albums that go from the current album to the top level album.
		// Once the stack is built we'll then add these albums to the treeview so that the full heirarchy
		// to the current album is shown.
		Pair<Deque<AlbumBo>, TreeNode> albumParents = getAlbumsBetweenTopLevelNodeAndAlbum(album);

		if (albumParents.getRight() == null)
		  return;

		bindSpecificAlbumToTreeview(albumParents.getRight(), albumParents.getLeft());
	  }
	}

	/// <summary>
	/// Bind the hierarchical list of albums to the specified treeview node.
	/// </summary>
	/// <param name="existingParentNode">The treeview node to add the first album in the stack to.</param>
	/// <param name="albumParents">A list of albums where the first album should be a child of the specified treeview
	/// node, and each subsequent album is a child of the previous album.</param>
	private void bindSpecificAlbumToTreeview(TreeNode existingParentNode, Deque<AlbumBo> albumParents) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException	{
	  // Assumption: The first album in the stack is a child of the existingParentNode node.
	  existingParentNode.setExpanded(true);

	  // For each album in the heirarchy of albums to the current album, add the album and all its siblings to the 
	  // treeview.
      for (AlbumBo album : albumParents) {
		if (existingParentNode.getNodes().size() == 0){
		  // Add all the album's siblings to the treeview.
			List<ContentObjectBo> childAlbums = AlbumUtils.loadAlbumInstance(StringUtils.toInteger(existingParentNode.getDataId()), true).getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList();
		  bindAlbumToTreeview(childAlbums, existingParentNode, false);
		}

		// Now find the album in the siblings we just added that matches the current album in the stack.
		// Set that album as the new parent and expand it.
		TreeNode nodeInAlbumHeirarchy = null;
		for (TreeNode node : existingParentNode.getNodes())	{
		  if (node.getDataId().equals(Long.toString(album.getId()))) {
			nodeInAlbumHeirarchy = node;
			nodeInAlbumHeirarchy.setExpanded(true);
			break;
		  }
		}

		if (nodeInAlbumHeirarchy == null)
		  throw new UnsupportedOperationException(MessageFormat.format("Album ID {0} is not a child of the treeview node representing album ID {1}.", album.getId(), existingParentNode.getDataId()));

		existingParentNode = nodeInAlbumHeirarchy;
	  }
	  existingParentNode.setExpanded(false);
	}

	/// <summary>
	/// Retrieve a list of albums that are in the heirarchical path between the specified album and a node in the treeview.
	/// The node that is discovered as the ancestor of the album is assigned to the existingParentNode parameter.
	/// </summary>
	/// <param name="album">An album. This method navigates the ancestors of this album until it finds a matching node in the treeview.</param>
	/// <param name="existingParentNode">The existing node in the treeview that is an ancestor of the specified album is assigned to
	/// this parameter.</param>
	/// <returns>Returns a list of albums where the first album (the one returned by calling Pop) is a child of the album 
	/// represented by the existingParentNode treeview node, and each subsequent album is a child of the previous album.
	/// The final album is the same album specified in the album parameter.</returns>
	private Pair<Deque<AlbumBo>, TreeNode> getAlbumsBetweenTopLevelNodeAndAlbum(AlbumBo album)	{
	  if (this.tv.getNodes().size() == 0)
		throw new ArgumentException("The treeview must have at least one top-level node before calling the function GetAlbumsBetweenTopLevelNodeAndAlbum().");

	  Deque<AlbumBo> albumParents = new ArrayDeque<AlbumBo>();
	  albumParents.push(album);

	  AlbumBo parentAlbum = (AlbumBo) album.getParent();

	  albumParents.push(parentAlbum);

	  // Navigate up from the specified album until we find an album that exists in the treeview. Remember,
	  // the treeview has been built with the root node and the first level of albums, so eventually we
	  // should find an album. If not, just return without showing the current album.
	  TreeNode existingParentNode = null;
	  while ((existingParentNode = this.tv.findNodeByDataId(Long.toString(parentAlbum.getId()))) == null) {
		parentAlbum = Reflections.as(AlbumBo.class, parentAlbum.getParent());

		if (parentAlbum == null)
		  break;

		albumParents.push(parentAlbum);
	  }

	  // Since we found a node in the treeview we don't need to add the most recent item in the stack. Pop it off.
	  albumParents.pop();

	  return new ImmutablePair<Deque<AlbumBo>, TreeNode>(albumParents, existingParentNode);
	}

	/// <summary>
	/// Gets a list of top-level albums to display in the treeview. There will be a maximum of one for each gallery.
	/// If the <see cref="TreeViewOptions.RootAlbumId" /> property is assigned, that album is returned and the <see cref="Galleries" /> property is 
	/// ignored.
	/// </summary>
	/// <returns>Returns a list of top-level albums to display in the treeview.</returns>
	private List<AlbumBo> getRootAlbums() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
	  List<AlbumBo> rootAlbums = new ArrayList<AlbumBo>(1);

	  if (this.tvOptions.RootAlbumId > 0)  {
		rootAlbums.add(AlbumUtils.loadAlbumInstance(this.tvOptions.RootAlbumId, true));
	  } else  {
		for (GalleryBo gallery : this.tvOptions.Galleries)		{
			AlbumBo rootAlbum = CMUtils.loadRootAlbum(gallery.getGalleryId(), RoleUtils.getMDSRolesForUser(), UserUtils.isAuthenticated());

		  if (rootAlbum != null)
			rootAlbums.add(rootAlbum);
		}
	  }

	  return rootAlbums;
	}

	private String getRootAlbumTitle(AlbumBo rootAlbum) throws InvalidGalleryException	{
	  GalleryBo gallery = CMUtils.loadGallery(rootAlbum.getGalleryId());
	  String rootAlbumPrefix = this.tvOptions.RootAlbumPrefix.replace("{GalleryRootAlbumPrefix}", gallery.getRootAlbumPrefix()).replace("{GalleryDescription}", gallery.getDescription());
	  return HelperFunctions.removeHtmlTags(StringUtils.join(rootAlbumPrefix, rootAlbum.getTitle().replace("{album.root_Album_Default_Title}", I18nUtils.getMessage("album.root_Album_Default_Title"))));
	}

	//#endregion

}