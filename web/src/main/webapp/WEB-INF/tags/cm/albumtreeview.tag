<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/tagcommlibs.jsp"%>
<%@ tag import="com.mds.cm.content.GalleryBoCollection" %>
<%@ tag import="com.mds.cm.content.AlbumBo" %>
<%@ tag import="com.mds.core.UiTemplateType" %>
<%@ tag import="com.mds.core.SecurityActions" %>
<%@ tag import="com.mds.util.StringUtils" %>
<%@ tag import="com.mds.cm.util.CMUtils" %>
<%@ tag import="com.mds.cm.util.AlbumTreeViewBuilder" %>
<%@ tag import="com.mds.cm.rest.TreeViewOptions" %>
<%@ tag import="com.mds.cm.rest.TreeView" %>
<%@ tag import="com.mds.common.mapper.JsonMapper" %>

<%@ attribute name="treeViewClientId" type="java.lang.String" required="true"%>
<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="allowMultiCheck" type="java.lang.Boolean" required="true"%>
<%@ attribute name="disabledCascadeUp" type="java.lang.Boolean" required="false"%>
<%@ attribute name="treeViewTheme" type="java.lang.String" required="true"%>
<%@ attribute name="rootAlbumPrefix" type="java.lang.String" required="false"%>
<%@ attribute name="rootAlbumId" type="java.lang.Long" required="false"%>
<%@ attribute name="galleryId" type="java.lang.Long" required="false"%>
<%@ attribute name="requiredSecurityPermissions" type="java.lang.Integer" required="true"%>
<%@ attribute name="navigateUrl" type="java.lang.String" required="false"%>
<%@ attribute name="enableCheckboxPlugin" type="java.lang.Boolean" required="true"%>
<%@ attribute name="requireAlbumSelection" type="java.lang.Boolean" required="false"%>
<%@ attribute name="selectedAlbumIds" type="com.mds.core.LongCollection" required="false"%>
<%@ attribute name="galleries" type="com.mds.cm.content.GalleryBoCollection" required="false"%>
 
<div id="${treeViewClientId}"></div>
<input type="hidden" id="hdnCheckedAlbumIds" name="hdnCheckedAlbumIds"/>

<%
	if (galleries == null){
		galleries = new GalleryBoCollection();

        galleries.add(CMUtils.loadGallery(galleryId));
	}
	if (rootAlbumId == null)
		rootAlbumId = Long.MIN_VALUE;
	
	if (StringUtils.isBlank(rootAlbumPrefix))
		rootAlbumPrefix = StringUtils.EMPTY;
	
	if (StringUtils.isBlank(navigateUrl))
		navigateUrl = StringUtils.EMPTY;
	
	if (disabledCascadeUp == null)
		disabledCascadeUp = false;
		
	//String treeDataJson = getTreeData()
	String scripts =  StringUtils.format(
"<script>\n" + 			
"	(function ($) {\n" + 
"   	$(document).ready(function() {\n" +
"    		var options = {\n" +
"      			allowMultiSelect: {1},\n" +
"      			disabledCascadeUp: {8},\n" +
"      			checkedAlbumIdsHiddenFieldClientId: '{2}',\n" +
"      			theme: '{3}',\n" +
"      			requiredSecurityPermissions: {4},\n" +
"      			navigateUrl: '{5}',\n" +
"      			enableCheckboxPlugin: {6}\n" +
"    		};\n" +
"    		$('#{0}').mdsTreeView({7}, options);\n" +
"  		});\n" +
"	})(jQuery);\n" +
"</script>\n"
		,
		 treeViewClientId, // 0
		 allowMultiCheck.toString().toLowerCase(), // 1
		 "hdnCheckedAlbumIds", // 2
		 treeViewTheme, // 3
		 requiredSecurityPermissions, // 4
		 navigateUrl, // 5
		 enableCheckboxPlugin.toString().toLowerCase(), // 6
		 getTreeData(), // 7
		 disabledCascadeUp.toString().toLowerCase()// 8
 	);

	//out.write(script);
	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", scripts);
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + scripts));
	}
%>

<%!
/// <summary>
/// Gets a JSON-formatted string of data that can be assigned to the data property of a 
/// jsTree jQuery instance.
/// </summary>
/// <returns>A string formatted as JSON.</returns>
/// <exception cref="System.InvalidOperationException">Thrown when one or more business rules
/// are violated.</exception>
private String getTreeData() throws Exception{
  //#region Validation

  if (allowMultiCheck && selectedAlbumIds.size() > 1) {
    throw new UnsupportedOperationException("The property AllowMultiCheck must be false when multiple album IDs have been assigned to the property SelectedAlbumIds.");
  }

  if (!SecurityActions.isValidSecurityAction(requiredSecurityPermissions)) {
    throw new UnsupportedOperationException("The property MDS.Web.Controls.albumtreeview.RequiredSecurityPermissions must be assigned before the TreeView can be rendered.");
  }

  //#endregion

  TreeViewOptions tvOptions = new TreeViewOptions();
  tvOptions.SelectedAlbumIds = selectedAlbumIds;
  tvOptions.NavigateUrl = navigateUrl;
  tvOptions.EnableCheckboxPlugin = enableCheckboxPlugin;
  tvOptions.RequiredSecurityPermissions = SecurityActions.parseSecurityAction(requiredSecurityPermissions).toArray(new SecurityActions[0]);
  tvOptions.RootAlbumId = rootAlbumId;
  tvOptions.RootAlbumPrefix = rootAlbumPrefix;
  tvOptions.Galleries = galleries;

  TreeView tv = AlbumTreeViewBuilder.getAlbumsAsTreeView(tvOptions);

  return tv.toJson();
}
%>