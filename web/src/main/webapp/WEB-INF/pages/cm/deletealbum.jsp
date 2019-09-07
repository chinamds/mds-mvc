<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="task.deleteAlbum.Page_Title"/></title>
    <meta name="menu" content="AlbumMenu"/>
    <meta name="heading" content="<fmt:message key='task.deleteAlbum.Header_Text'/>"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/deletealbum.js"%>
</c:set>

<div class="col-sm-12 mds_content">
	<%@ include file="/common/messages.jsp" %>

	<form id="deleteAlbumForm" method="post" action="<c:url value='/cm/deletealbum'/>">
		<input type="hidden" id="gid"  name="gid" value="<c:out value="${galleryId}"/>"/>
		<input type="hidden" id="albumId"  name="albumId" value="<c:out value="${albumId}"/>"/>
		<input type="hidden" id="moid"  name="moid" value="<c:out value="${param.moid}"/>"/>
	
	
		<fmt:message key="task.deleteAlbum.Header_Text" var="taskHeader"/>
		<fmt:message key="task.deleteAlbum.Body_Text" var="taskBody"/>
		<fmt:message key="task.deleteAlbum.Ok_Button_Text" var="task_Ok_Button_Text"/>
		<fmt:message key="task.deleteAlbum.Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
		
		<ts:taskheader taskHeader="${taskHeader }" taskBody="${ taskBody}" task_Ok_Button_Text="${task_Ok_Button_Text}" />
		<p class="mds_textcol mds_msgwarning"><fmt:message key="task.deleteAlbum.Warning"/></p>
		<p>
			<div class="form-group checkbox">
		         <label>
	                 <input type="checkbox" id="chkDeleteDbRecordsOnly" name="chkDeleteDbRecordsOnly"><fmt:message key="task.deleteAlbum.DeleteDbRecordsOnly_Lbl"/>
	             </label>
		    </div> 
		</p>
		<div class="form-group hidden d-none">
		    <button type="submit" id="deleteAlbum" name="deleteAlbum" class="btn btn-large btn-primary">
		        <i class="fa fa-check icon-white"></i> <fmt:message key='button.save'/>
		    </button>
		    <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
				            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
			</button>
		</div>
			
	    
	    <ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
    </form>
</div>