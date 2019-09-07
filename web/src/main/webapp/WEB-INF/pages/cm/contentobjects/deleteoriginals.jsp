<%@ include file="/common/taglibs.jsp"%>

<%@ page import="com.mds.cm.util.TransferObjectState" %>
<%@ page import="com.mds.core.ResourceId" %>
<%@ page import="com.mds.util.Utils" %>

<head>
    <title><fmt:message key="task.deleteOriginal.Page_Title"/></title>
    <meta name="menu" content="AddContentMenu"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
    <meta name="heading" content="<fmt:message key='task.deleteOriginal.heading'/>"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/contentobjects/deleteoriginal.js"%>
</c:set>

<div class="col-sm-12 mds_content">
	<%@ include file="/common/messages.jsp" %>
	
	<form id="deleteOriginalForm" method="post" action="<c:url value='/cm/contentobjects/deleteoriginals'/>">
	<input type="hidden" id="gid"  name="gid" value="<c:out value="${galleryId}"/>"/>
	<input type="hidden" id="albumId"  name="albumId" value="<c:out value="${albumId}"/>"/>
	<input type="hidden" id="userCanDeleteContentObject"  name="userCanDeleteContentObject" value="<c:out value="${userCanDeleteContentObject}"/>"/>
	<input type="hidden" id="userCanDeleteChildAlbum"  name="userCanDeleteChildAlbum" value="<c:out value="${userCanDeleteChildAlbum}"/>"/>
	<input type="hidden" id="moid"  name="moid" value="<c:out value="${param.moid}"/>"/>
	<fmt:message key="task.deleteOriginal.Header_Text" var="taskHeader"/>
	<fmt:message key="task.deleteOriginal.Body_Text" var="taskBody"/>
	<fmt:message key="task.deleteOriginal.Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.deleteOriginal.Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
	
	<ts:taskheader taskHeader="${taskHeader }" taskBody="${ taskBody}" task_Ok_Button_Text="${task_Ok_Button_Text}" />
	<input type="hidden" id="hdnCheckedContentObjectIds" name="hdnCheckedContentObjectIds" value="<c:out value="${hdnCheckedContentObjectIds}"/>" />
	<div class="dcm_addleftpadding1">
		<p class="dcm_textcol dcm_msgwarning" style="border-bottom: #369 1px solid;">
			<fmt:message key="task.deleteOriginal.Warning"/>
		</p>
		<p class="dcm_h3">
			<fmt:message key="task.deleteOriginal.Potential_Savings_Text"><fmt:param value="${totalFileSizeKbAllOriginalFiles}"/><fmt:param value="${totalFileSizeKbAllOriginalFiles/1024}"/></fmt:message>
		</p>
		<p class="dcm_textcol">
			<fmt:message key="task.deleteOriginal.Potential_Savings_Hdr"/>
		</p>
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
		    <button type="submit" id="deleteOriginal" name="deleteOriginal" class="btn btn-large btn-primary">
		        <i class="fa fa-check icon-white"></i> <fmt:message key='button.save'/>
		    </button>
		    <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
				            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
			</button>
	</div>
    
    <ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
    </form>
</div>