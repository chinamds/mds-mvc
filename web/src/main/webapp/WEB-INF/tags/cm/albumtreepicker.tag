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

<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="keyName" type="java.lang.String" required="true" description="hidden field（ID）"%>
<%@ attribute name="keyValue" type="java.lang.String" required="true" description="hidden field value（ID）"%>
<%@ attribute name="fieldName" type="java.lang.String" required="true" description="input field（Name）"%>
<%@ attribute name="fieldValue" type="java.lang.String" required="true" description="input file name（Name）"%>
<%@ attribute name="allowMultiCheck" type="java.lang.Boolean" required="true"%>
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

<%@ attribute name="cssClass" type="java.lang.String" required="false" description="css class"%>
<%@ attribute name="cssStyle" type="java.lang.String" required="false" description="css style"%>
<%@ attribute name="smallBtn" type="java.lang.Boolean" required="false" description="small button"%>
<%@ attribute name="hideBtn" type="java.lang.Boolean" required="false" description="Hide select button"%>
<%@ attribute name="disabled" type="java.lang.String" required="false" description="disable zTree Picker widget"%>
<%@ attribute name="dataMsgRequired" type="java.lang.String" required="false" description=""%>
<%@ attribute name="maxlength" type="java.lang.String" required="false" description="max length for text input"%>

<form class="form-horizontal">
<div class="form-group row">
	<label for="${id}Name" class="col-sm-2 col-xs-2 control-label"><fmt:message key="albumtreepicker.selectfolder"/></label>        
	<div class="col-sm-9 col-xs-9 input-group dropdown">
		<input id="${id}Id" name="${keyName}" type="hidden" value="${keyValue}"/>
		<input id="${id}Name" name="${fieldName}" ${allowInput?'':'readonly="readonly"'} type="text" value="${fns:unescapeHtml(fieldValue)}" data-msg-required="${dataMsgRequired}"
			class="${cssClass}" style="${cssStyle}" maxlength="${not empty maxlength?maxlength:50}"/>
		<span class="input-group-btn">
			<a id="${id}Button" type="button" href="javascript:" class="btn btn-default dropdown-toggle ${disabled} ${hideBtn ? 'hide' : ''}" data-toggle="dropdown"><i class="fa fa-chevron-down"></i></a>
			<ul class="dropdown-menu dropdown-menu-right"><li id="liTreeContent${id}"><div id="${id}TreeView"></div></li></ul>
		</span>
	</div>  
</div>

<%-- <div id="${id}ThumbView" class="row"></div> --%>
<div class="row mds_ns">
<ul id="${id}ThumbView" class="mds_floatcontainer">
</ul>
</div>
</form>

<%
	if (galleries == null){
		galleries = new GalleryBoCollection(); //CMUtils.loadLoginUserGalleries(); 
		if (galleryId != null){
        	galleries.add(CMUtils.loadGallery(galleryId));
		}
	}
	if (rootAlbumId == null)
		rootAlbumId = Long.MIN_VALUE;
	
	if (StringUtils.isBlank(rootAlbumPrefix))
		rootAlbumPrefix = StringUtils.EMPTY;
	
	if (StringUtils.isBlank(navigateUrl))
		navigateUrl = StringUtils.EMPTY;
		
	//String treeDataJson = getTreeData()
	String scripts =  StringUtils.format(
"<script>\n" + 			
"	(function ($) {\n" + 
"   	$(document).ready(function() {\n" +
"    		var options = {\n" +
"				clientId: '{15}',\n" +
"      			allowMultiSelect: {1},\n" +
"      			checkedAlbumIdsHiddenFieldClientId: '{2}',\n" +
"      			checkedAlbumNamesFieldClientId: '{11}',\n" +
"      			theme: '{3}',\n" +
"      			requiredSecurityPermissions: {4},\n" +
"      			navigateUrl: '{5}',\n" +
"      			enableCheckboxPlugin: {6},\n" +
"      			selectChanged: function(nodes) {\n" +
"	        		  selectChanged(nodes);\n" +
"		          }\n" +		
"    		};\n" +
"    		$('#{0}').mdsTreePicker({7}, options);\n" +
"			$('#{0}').css({\n" +
"			    width: $('#{8}').outerWidth() + $('#{9}').outerWidth()\n" +
"			});\n" +
"			$('#{10}').on('click', function (e) {\n" +
"			    e.preventDefault;\n" +
"			    this.blur();\n" +
"			    e.stopPropagation();\n" +
"			});\n" +
/* "			var thumbOptions = {\n" +
"      			allowMultiSelect: {1},\n" +
"      			checkedContentItemIdsHiddenFieldClientId: '{2}',\n" +
"      			requiredSecurityPermissions: {4},\n" +
"      			thumbPickerUrl: '{12}',\n" +
"      			albumIdsToSelect: [{13}]\n" +
"    		};\n" + //
"    		$('#{14}').mdsThumbPicker(null, thumbOptions);\n" +  */
"  		});\n" +
/* "    		$('#{14} .thmb').equalSize();\n" + // Make all thumbnail tags the same width & height
"   		$('#{14} .mds_go_t').css('width', '').css('display', '');\n" + // Remove the width that was initially set, allowing title to take the full width of thumbnail */
"	})(jQuery);\n" +
"   var selectChanged = function(nodes) {\n" +
"		var thumbOptions = {\n" +
"      			allowMultiSelect: {1},\n" +
"      			checkedContentItemIdsHiddenFieldClientId: '{2}',\n" +
"      			requiredSecurityPermissions: {4},\n" +
"      			thumbPickerUrl: '{12}',\n" +
"      			albumIdsToSelect: nodes\n" +
"    	};\n" + //
"    	$('#{14}').mdsThumbPicker(null, thumbOptions);\n" +
"       $('.btn-selectallthumb').removeClass('active');\n" +
"   };\n" +
"</script>\n"
		,
		 id + "TreeView", // 0
		 allowMultiCheck.toString().toLowerCase(), // 1
		 id + "Id", // 2
		 treeViewTheme, // 3
		 requiredSecurityPermissions, // 4
		 navigateUrl, // 5
		 enableCheckboxPlugin.toString().toLowerCase(), // 6
		 getTreeData(), // 7
		 id + "Name", //8
		 id + "Button", //9
		 "liTreeContent" + id,   //10
		 id + "Name", // 11
		 request.getContextPath() + "/services/api/albumrests/gettreepicker", //12
		 selectedAlbumIds.get(0), //13
		 id + "ThumbView", //14
		 id //15
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

	/*private String genThumbView() throws Exception{
		if (allowMultiCheck && selectedAlbumIds.size() > 1) {
			throw new UnsupportedOperationException("The property AllowMultiCheck must be false when multiple album IDs have been assigned to the property SelectedAlbumIds.");
		}
		
		if (!SecurityActions.isValidSecurityAction(requiredSecurityPermissions)) {
			throw new UnsupportedOperationException("The property MDS.Web.Controls.albumtreeview.RequiredSecurityPermissions must be assigned before the TreeView can be rendered.");
		}

	  	//#endregion
	  	String html = 
"		<div class='col-sm-6 col-md-4'>\n" + 	
"	    <div class='thumbnail'>\n" + 	  
"	      <img src='{0}' alt='{1}'>\n" + 	
"	      <div class='caption'>\n" + 	
"	        <h3>Thumbnail label</h3>\n" + 	
"	        <p>...</p>\n" + 	
"	        <p><label class='checkbox'><input type='checkbox' value='{}' id='{}'>{}</label></p>\n" + 	
"	      </div>\n" + 	
"	    </div>\n" + 	
"	  </div>\n";

		return html;
	}*/
%>