<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag import="com.mds.aiotplayer.util.StringUtils" %>
<%@ tag import="com.mds.aiotplayer.util.HelperFunctions" %>
<%@ tag import="com.mds.aiotplayer.common.mapper.JsonMapper" %>
<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="mdsClientId" type="java.lang.String" required="true"%>
<%@ attribute name="galleryView" type="com.mds.aiotplayer.cm.util.GalleryView" required="true" description="gallery View customize" %>
<%@ attribute name="mdsData" type="com.mds.aiotplayer.cm.rest.CMData" required="false"%>
<%@ attribute name="albumTreeData" type="com.mds.aiotplayer.cm.rest.TreeView" required="false"%>
<%@ attribute name="showLeftPaneForAlbum" type="java.lang.Boolean" required="true"%>
<%@ attribute name="showLeftPaneForContentObject" type="java.lang.Boolean" required="true"%>

 <%	
 	String script = StringUtils.format( 
"   <script>\n" + 			
"   window.{0} = {};\n" + 
"   window.{0}.p = function () { return $('#{0}'); };\n" +
"   window.{0}.mdsData = $.parseJSON('{2}');\n" +
"   window.{0}.mdsData.ActiveMetaItems = (window.{0}.mdsData.MediaItem ? window.{0}.mdsData.MediaItem.MetaItems : window.{0}.mdsData.Album.MetaItems) || [];\n" +
"   window.{0}.mdsData.ActiveApprovalItems = (window.{0}.mdsData.MediaItem ? window.{0}.mdsData.MediaItem.ApprovalItems : window.{0}.mdsData.Album.ApprovalItems) || [];\n" +
"   window.{0}.mdsData.ActiveContentItems = (window.{0}.mdsData.MediaItem ? [window.Mds.convertMediaItemToContentItem(window.{0}.mdsData.MediaItem)] : [window.Mds.convertAlbumToContentItem(window.{0}.mdsData.Album)]) || [];\n" +
"   {1}\n" +
"   </script>\n"
	,
			            mdsClientId, // 0
			            galleryView.getAlbumTreeDataClientScript(), // 1
			            galleryView.getClientMdsDataAsJson() //2
			            //HelperFunctions.jsEncode(JsonMapper.getInstance().toJson(galleryView.get)) //2
			            );

	//out.write(script);
	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", script);
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + script));
	}
%>
 
<%!
	private String getAlbumTreeDataClientScript(){
		if (showLeftPaneForAlbum || showLeftPaneForContentObject){
			return StringUtils.format("window.{0}.mdsAlbumTreeData = $.parseJSON('{1}');", mdsClientId, HelperFunctions.jsEncode(albumTreeData.toJson()));
		}
	
		return StringUtils.EMPTY;
	}
%>