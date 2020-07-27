<%@ include file="/common/taglibs.jsp"%>

<%@ page import="com.mds.aiotplayer.cm.util.TransferObjectState" %>
<%@ page import="com.mds.aiotplayer.core.ResourceId" %>
<%@ page import="com.mds.aiotplayer.util.Utils" %>

<head>
    <title><fmt:message key="task.downloadObjects.Page_Title"/></title>
    <meta name="menu" content="AddContentMenu"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
    <meta name="heading" content="<fmt:message key='task.downloadObjects.heading'/>"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/contentobjects/downloadobject.js"%>
</c:set>

<div class="col-sm-12 mds_content">
	<%@ include file="/common/messages.jsp" %>
	
	<form id="downloadObjectForm" method="post" action="<c:url value='/cm/contentobjects/downloadobjects'/>">
	<input type="hidden" id="gid"  name="gid" value="<c:out value="${galleryId}"/>"/>
	<input type="hidden" id="albumId"  name="albumId" value="<c:out value="${albumId}"/>"/>
	<input type="hidden" id="enableContentObjectZipDownload"  name="enableContentObjectZipDownload" value="<c:out value="${enableContentObjectZipDownload}"/>"/>
	<input type="hidden" id="enableAlbumZipDownload"  name="enableAlbumZipDownload" value="<c:out value="${enableAlbumZipDownload}"/>"/>
	<input type="hidden" id="moid"  name="moid" value="<c:out value="${param.moid}"/>"/>
	<fmt:message key="task.downloadObjects.Header_Text" var="taskHeader"/>
	<fmt:message key="task.downloadObjects.Body_Text" var="taskBody"/>
	<fmt:message key="task.downloadObjects.Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.downloadObjects.Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
	
	<ts:taskheader taskHeader="${taskHeader }" taskBody="${ taskBody}" task_Ok_Button_Text="${task_Ok_Button_Text}" />
	<input type="hidden" id="hdnCheckedContentObjectIds" name="hdnCheckedContentObjectIds" value="<c:out value="${hdnCheckedContentObjectIds}"/>" />
	<div class="form-group row">
          <label for="ddlImageSize" class="col-sm-2 col-form-label"><fmt:message key="task.downloadObjects.Select_Image_Size_Label_Text"/></label>
          <div class="col-sm-4">
	          <select id="ddlImageSize" name="ddlImageSize" class="form-control">
	              <c:forEach items="${imageSizes}" var="imageSize">
	              <option value="${imageSize.size}" selected="selected">${imageSize.info}</option>
	              </c:forEach>
	          </select>
          </div>
    </div>
	<p>
	    <a class="chkCheckUncheckAll" href='#' data-ischecked="false">
	      <fmt:message key="site.toggleCheckAll_Lbl"/>
	    </a>
    </p>
	<div class="form-group" style="margin-left: 10px;">
		<ul id="${galleryView.mdsClientId}_ThumbView" class="mds_floatcontainer">
		</ul>
	</div>

	<div class="form-group hidden d-none">
		    <button type="submit" id="downloadObjects" name="downloadObjects" class="btn btn-large btn-primary">
		        <i class="fa fa-check icon-white"></i> <fmt:message key='button.save'/>
		    </button>
		    <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
				            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
			</button>
	</div>
    
    <ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
    </form>
</div>