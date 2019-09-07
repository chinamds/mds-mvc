<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="albumDetail.title"/></title>
    <meta name="menu" content="AlbumMenu"/>
    <meta name="heading" content="<fmt:message key='albumDetail.heading'/>"/>
    <meta name="container" content="galleryView"/>
    <meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />

<div class="col-sm-3">
    <h2><fmt:message key="albumDetail.heading"/></h2>
    <fmt:message key="albumDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="album" method="post" action="createalbum" cssClass="well"
           id="albumForm" onsubmit="return validateAlbum(this)">
            <input type="hidden" name="aid" value="<c:out value="${param.aid}"/>"/>
            <input type="hidden" name="gid" value="<c:out value="${param.gid}"/>"/>
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
<form:hidden path="dateEnd"/>
<form:hidden path="dateStart"/>
<form:hidden path="isPrivate"/>
<form:hidden path="ownedBy"/>
<form:hidden path="ownerRoleName"/>
<form:hidden path="seq"/>
<form:hidden path="sortAscending"/>
<form:hidden path="sortByMetaName"/>
<c:if test="${not empty album.parent}">
<form:hidden path="parent.id"/>
</c:if>
    <%-- <spring:bind path="album.dateEnd">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.dateEnd" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateEnd" id="dateEnd"  maxlength="19"/>
        <form:errors path="dateEnd" cssClass="help-block"/>
    </div>
    <spring:bind path="album.dateStart">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.dateStart" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateStart" id="dateStart"  maxlength="19"/>
        <form:errors path="dateStart" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="gallery" items="galleryList" itemLabel="label" itemValue="value"/>
    <spring:bind path="album.isPrivate">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.isPrivate" styleClass="control-label"/>
        <form:checkbox path="isPrivate" id="isPrivate" cssClass="checkbox"/>
        <form:errors path="isPrivate" cssClass="help-block"/>
    </div> --%>
<%--     <div class="form-group">
		<label for="albumName" class="control-label"><fmt:message key="album.name"/></label>
		<cm:albumtreepicker id="album" keyName="albumId" keyValue="" fieldName="albumName" fieldValue=""
		cssClass="required form-control" maxlength="100" allowMultiCheck="false" treeViewTheme="mds" requiredSecurityPermissions="${requiredSecurityPermissions}" galleryId="${galleryId}"
	    enableCheckboxPlugin="false" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}"/>
	</div> --%>
	
    <spring:bind path="album.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="255"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <%-- <spring:bind path="album.ownedBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.ownedBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="ownedBy" id="ownedBy"  maxlength="256"/>
        <form:errors path="ownedBy" cssClass="help-block"/>
    </div>
    <spring:bind path="album.ownerRoleName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.ownerRoleName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="ownerRoleName" id="ownerRoleName"  maxlength="256"/>
        <form:errors path="ownerRoleName" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="parent" items="parentList" itemLabel="label" itemValue="value"/>
    <spring:bind path="album.seq">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.seq" styleClass="control-label"/>
        <form:input cssClass="form-control" path="seq" id="seq"  maxlength="255"/>
        <form:errors path="seq" cssClass="help-block"/>
    </div>
    <spring:bind path="album.sortAscending">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.sortAscending" styleClass="control-label"/>
        <form:checkbox path="sortAscending" id="sortAscending" cssClass="checkbox"/>
        <form:errors path="sortAscending" cssClass="help-block"/>
    </div>
    <spring:bind path="album.sortByMetaName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="album.sortByMetaName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="sortByMetaName" id="sortByMetaName"  maxlength="255"/>
        <form:errors path="sortByMetaName" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="thumbContentObject" items="thumbContentObjectList" itemLabel="label" itemValue="value"/> --%>
<%--     <cm:albumtreeview id="${mdsClientId}_treeview" treeViewClientId="${mdsClientId}_tvContainer" allowMultiCheck="false" 
    treeViewTheme="mds" rootAlbumPrefix="" rootAlbumId="" requiredSecurityPermissions="${requiredSecurityPermissions}" navigateUrl="" 
    enableCheckboxPlugin="true" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}" galleries=""/> --%>
    <div class="form-group">
	    <cm:albumtreeview id="${galleryView.mdsClientId}_treeview" treeViewClientId="${galleryView.mdsClientId}_tvContainer" allowMultiCheck="false" 
	    treeViewTheme="mds" requiredSecurityPermissions="${requiredSecurityPermissions}" galleryId="${galleryId}"
	    enableCheckboxPlugin="true" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}"/>
    </div>
    
    <div class="form-group">
    	<secure:hasAnyPermissions name="cm:albums:add,cm:albums:addchild">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="album" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['albumForm']).focus();
    });
</script>
