<%@ include file="/common/taglibs.jsp"%>

<%@ page import="com.mds.core.ResourceId" %>
<%@ page import="com.mds.util.Utils" %>

<head>
    <title><fmt:message key="task.rotateImages.Page_Title"/></title>
    <meta name="menu" content="AddContentMenu"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
    <meta name="heading" content="<fmt:message key='task.rotateImages.heading'/>"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/contentobjects/rotateimages.js"%>
</c:set>

<div class="col-sm-12 mds_content">
	<%@ include file="/common/messages.jsp" %>
	
	<form id="rotateImagesForm" method="post" action="<c:url value='/cm/contentobjects/rotateimages'/>">
	<input type="hidden" id="gid"  name="gid" value="<c:out value="${galleryId}"/>"/>
	<input type="hidden" id="albumId"  name="albumId" value="<c:out value="${albumId}"/>"/>
	<%-- <input type="hidden" id="contentObjectId"  name="contentObjectId" value="<c:out value="${contentObjectId}"/>"/> --%>
	<input type="hidden" id="moid"  name="moid" value="<c:out value="${param.moid}"/>"/>
	<fmt:message key="task.rotateImages.Header_Text" var="taskHeader"/>
	<fmt:message key="task.rotateImages.Body_Text" var="taskBody"/>
	<fmt:message key="task.rotateImages.Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.rotateImages.Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
	
	<ts:taskheader taskHeader="${taskHeader }" taskBody="${ taskBody}" task_Ok_Button_Text="${task_Ok_Button_Text}" />
	<input type="hidden" id="hdnCheckedContentObjectIds" name="hdnCheckedContentObjectIds" value="<c:out value="${hdnCheckedContentObjectIds}"/>" />
	<input type="hidden" id="hdnNewSides" name="hdnNewSides" value="<c:out value="${hdnNewSides}"/>" />
	<div class="form-group" style="margin-left: 10px;">
		<div class="mds_floatcontainer">
			<ts:rotateimage galleryView="${galleryView}" multiRotate="true"/>
		</div>
	</div>

	<div class="form-group hidden d-none">
		    <button type="submit" id="rotateImages" name="rotateImages" class="btn btn-large btn-primary">
		        <i class="fa fa-check icon-white"></i> <fmt:message key='button.save'/>
		    </button>
		    <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
				            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
			</button>
	</div>
    
    <ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
    </form>
</div>