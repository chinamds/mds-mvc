<%@ include file="/common/taglibs.jsp"%>

<%@ page import="com.mds.cm.util.TransferObjectState" %>
<%@ page import="com.mds.core.ResourceId" %>
<%@ page import="com.mds.util.Utils" %>

<head>
    <title><fmt:message key="task.deleteObjects.Page_Title"/></title>
    <meta name="menu" content="AddContentMenu"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
    <meta name="heading" content="<fmt:message key='task.deleteObjects.heading'/>"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/contentobjects/deleteobject.js"%>
</c:set>

<div class="col-sm-12 mds_content">
	<%@ include file="/common/messages.jsp" %>
	
	<form id="deleteObjectForm" method="post" action="<c:url value='/cm/contentobjects/deleteobjects'/>">
	<input type="hidden" id="gid"  name="gid" value="<c:out value="${galleryId}"/>"/>
	<input type="hidden" id="albumId"  name="albumId" value="<c:out value="${albumId}"/>"/>
	<input type="hidden" id="userCanDeleteContentObject"  name="userCanDeleteContentObject" value="<c:out value="${userCanDeleteContentObject}"/>"/>
	<input type="hidden" id="userCanDeleteChildAlbum"  name="userCanDeleteChildAlbum" value="<c:out value="${userCanDeleteChildAlbum}"/>"/>
	<input type="hidden" id="moid"  name="moid" value="<c:out value="${param.moid}"/>"/>
	<fmt:message key="task.deleteObjects.Header_Text" var="taskHeader"/>
	<fmt:message key="task.deleteObjects.Body_Text" var="taskBody"/>
	<fmt:message key="task.deleteObjects.Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.deleteObjects.Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
	
	<ts:taskheader taskHeader="${taskHeader }" taskBody="${ taskBody}" task_Ok_Button_Text="${task_Ok_Button_Text}" />
	<input type="hidden" id="hdnCheckedContentObjectIds" name="hdnCheckedContentObjectIds" value="<c:out value="${hdnCheckedContentObjectIds}"/>" />
	<p class="mds_textcol mds_msgwarning mds_addleftpadding1">
		<fmt:message key="task.deleteObjects.Warning"/>
	</p>
	<div class="form-group checkbox">
         <label>
                <input type="checkbox" id="chkDeleteDbRecordsOnly" name="chkDeleteDbRecordsOnly"><fmt:message key="task.deleteObjects.DeleteDbRecordsOnly_Lbl"/>
            </label>
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
		    <button type="submit" id="deleteObjects" name="deleteObjects" class="btn btn-large btn-primary">
		        <i class="fa fa-check icon-white"></i> <fmt:message key='button.save'/>
		    </button>
		    <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
				            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
			</button>
	</div>
    
    <ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
    </form>
</div>