<%@ include file="/common/taglibs.jsp"%>

<%@ page import="com.mds.aiotplayer.cm.util.TransferObjectState" %>
<%@ page import="com.mds.aiotplayer.core.ResourceId" %>
<%@ page import="com.mds.aiotplayer.util.Utils" %>

<head>
    <title><fmt:message key="task.transferObjects.Page_Title"/></title>
    <meta name="menu" content="AddContentMenu"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
    <meta name="heading" content="<fmt:message key='task.transferObjects.heading'/>"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/contentobjects/transferobject.js"%>
</c:set>

<div class="col-sm-12 mds_content">
	<%@ include file="/common/messages.jsp" %>
	<form id="transferObjectForm" method="post" action="<c:url value='/cm/contentobjects/transferobject'/>">
	<input type="hidden" id="gid"  name="gid" value="<c:out value="${galleryId}"/>"/>
	<input type="hidden" id="albumId"  name="albumId" value="<c:out value="${albumId}"/>"/>
	<input type="hidden" id="moid"  name="moid" value="<c:out value="${param.moid}"/>"/>
	<input type="hidden" id="skipstep1"  name="skipstep1" value="<c:out value="${param.skipstep1}"/>"/>
	<input type="hidden" id="tt"  name="tt" value="<c:out value="${param.tt}"/>"/>
	<input type="hidden" id="transferObjectState"  name="transferObjectState" value="<c:out value="${transferObjectState}"/>"/>
<c:if test="${transferObjectState == TransferObjectState.AlbumCopyStep2}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="0"/>	
	<fmt:message key="task.transferObjects.Copy_Album_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Copy_Album_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Copy_Album_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Copy_Album_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>

<c:if test="${transferObjectState == TransferObjectState.AlbumMoveStep2}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="0"/>	
	<fmt:message key="task.transferObjects.Move_Album_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Move_Album_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Move_Album_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Move_Album_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>

<c:if test="${transferObjectState == TransferObjectState.ContentObjectCopyStep2}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="0"/>	
	<fmt:message key="task.transferObjects.Copy_Content_Object_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Copy_Content_Object_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Copy_Content_Object_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Copy_Content_Object_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>

<c:if test="${transferObjectState == TransferObjectState.ContentObjectMoveStep2}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="0"/>	
	<fmt:message key="task.transferObjects.Move_Content_Object_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Move_Content_Object_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Move_Content_Object_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Move_Content_Object_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>

<c:if test="${transferObjectState == TransferObjectState.ObjectsCopyStep1}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="1"/>	
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step1_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step1_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step1_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step1_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>
<c:if test="${transferObjectState == TransferObjectState.ObjectsMoveStep1}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="1"/>	
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step1_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step1_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step1_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step1_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>

<c:if test="${transferObjectState == TransferObjectState.ObjectsCopyStep2}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="0"/>	
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step2_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step2_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step2_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Copy_Content_Objects_Step2_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>
<c:if test="${transferObjectState == TransferObjectState.ObjectsMoveStep2}">
	<input type="hidden" id="showNextPage" name="showNextPage" value="0"/>	
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step2_Header_Text" var="taskHeader"/>
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step2_Body_Text" var="taskBody"/>
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step2_Ok_Button_Text" var="task_Ok_Button_Text"/>
	<fmt:message key="task.transferObjects.Move_Content_Objects_Step2_Ok_Button_Tooltip" var="task_Ok_Button_Tooltip"/>
</c:if>

<c:if test="${transferObjectState == TransferObjectState.ReadyToTransfer}">	
</c:if>
	
	<ts:taskheader taskHeader="${taskHeader }" taskBody="${ taskBody}" task_Ok_Button_Text="${task_Ok_Button_Text}" />
	<input type="hidden" id="hdnCheckedContentObjectIds" name="hdnCheckedContentObjectIds" value="<c:out value="${hdnCheckedContentObjectIds}"/>" />
<c:if test="${not param.skipstep1 and (transferObjectState == TransferObjectState.ObjectsCopyStep1 or transferObjectState == TransferObjectState.ObjectsMoveStep1)}">	
	<p>
	    <a class="chkCheckUncheckAll" href='#' data-ischecked="false">
	      <fmt:message key="site.toggleCheckAll_Lbl"/>
	    </a>
    </p>
	<div class="form-group" style="margin-left: 10px;">
		<ul id="${galleryView.mdsClientId}_ThumbView" class="mds_floatcontainer">
		</ul>
	</div>
</c:if>	

<c:if test="${transferObjectState == TransferObjectState.AlbumCopyStep2 or transferObjectState == TransferObjectState.AlbumMoveStep2 
	or transferObjectState == TransferObjectState.ContentObjectCopyStep2 or transferObjectState == TransferObjectState.ContentObjectMoveStep2 
	or transferObjectState == TransferObjectState.ObjectsCopyStep2 or transferObjectState == TransferObjectState.ObjectsMoveStep2 or param.skipstep1}">			    
    <div class="form-group">
	    <cm:albumtreeview id="${galleryView.mdsClientId}_treeview" treeViewClientId="${galleryView.mdsClientId}_tvContainer" allowMultiCheck="false" 
	    treeViewTheme="mds" requiredSecurityPermissions="${requiredSecurityPermissions}" galleries="${galleries}" rootAlbumPrefix="${rootAlbumPrefix}"
	    enableCheckboxPlugin="true" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}"/>
    </div>
</c:if>
	<div class="form-group hidden d-none">
		    <button type="submit" id="transfer" name="transfer" class="btn btn-large btn-primary">
		        <i class="fa fa-check icon-white"></i> <fmt:message key='button.save'/>
		    </button>
		    <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
				            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
			</button>
	</div>
    
    <ts:taskfooter task_Ok_Button_Text="${task_Ok_Button_Text}" />
    </form>
</div>