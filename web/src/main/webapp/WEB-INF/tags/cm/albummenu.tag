<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ tag import="com.mds.aiotplayer.util.StringUtils" %>
<%@ tag import="com.mds.aiotplayer.cm.util.AlbumMenuBuilder" %>

<%-- <%@ attribute name="album" type="com.mds.aiotplayer.cm.content.AlbumBo" required="true" description="album folder" %> --%>
<%@ attribute name="galleryView" type="com.mds.aiotplayer.cm.util.GalleryView" required="true" description="gallery View customize" %>
<%-- <%@ attribute name="uiTemplate" type="com.mds.aiotplayer.cm.content.UiTemplateBo" required="true" description="ui template" %> --%>
<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="mdsClientId" type="java.lang.String" required="true"%>

<%
 	AlbumMenuBuilder albumMenuBuilder = new AlbumMenuBuilder(galleryView);
%>
<div class="albumMenuContainer d-flex">
    <%
	    if (albumMenuBuilder.getShowActionMenu()){
	        out.write(albumMenuBuilder.getMenuHtml(galleryView.getRequest()));
        }
	%>
  	<div class="<%= albumMenuBuilder.getAlbumMenuClass() %>">
	  	<div id="phMenu">
	  	<%
		  	out.write(albumMenuBuilder.buildMenuString(galleryView.getRequest()));
		 %>
		</div>
	</div>
 </div>
  
 <%-- <%
 	String scripts = 
"<script>\n" + 
"   (function ($) {\n" + 
"	    $(document).ready(function() {\n" + 
"	      $(\"#{0}_mnu\").menubar({\n" + 
"	        autoExpand: true\n" + 
"	      }).show();\n" + 
"	     \n" + 
"	      $('#{0}_mnu_logoff').click(function (e) {\n" + 
"	        e.preventDefault();\n" + 
"	        e.stopPropagation();\n" + 
"	        Mds.DataService.logOff(function (data) { Mds.ReloadPage(); });\n" + 
"	      });\n" + 
"	    });\n" + 
"  })(jQuery);\n" + 
"</script>\n";

	scripts = StringUtils.format(scripts, mdsClientId); 
	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", scripts);
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + scripts));
	}
 %> --%>
