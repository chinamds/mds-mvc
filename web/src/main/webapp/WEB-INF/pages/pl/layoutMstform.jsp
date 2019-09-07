<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="layoutMstDetail.title"/></title>
    <meta name="menu" content="LayoutMstMenu"/>
    <meta name="heading" content="<fmt:message key='layoutMstDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="layoutMstList.layoutMst"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="layoutMstDetail.heading"/></h2>
    <fmt:message key="layoutMstDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="layoutMst" method="post" action="layoutMstform" cssClass="well"
           id="layoutMstForm" onsubmit="return validateLayoutMst(this)">
<form:hidden path="id"/>
    <spring:bind path="layoutMst.createdBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.createdBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createdBy" id="createdBy"  maxlength="100"/>
        <form:errors path="createdBy" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.dateAdded">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.dateAdded" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateAdded" id="dateAdded"  maxlength="19"/>
        <form:errors path="dateAdded" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.dateLastModified">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.dateLastModified" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateLastModified" id="dateLastModified"  maxlength="19"/>
        <form:errors path="dateLastModified" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.lastModifiedBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.lastModifiedBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModifiedBy" id="lastModifiedBy"  maxlength="100"/>
        <form:errors path="lastModifiedBy" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL10Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL10Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL10Bottom" id="IL10Bottom"  maxlength="255"/>
        <form:errors path="IL10Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL10Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL10Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL10Left" id="IL10Left"  maxlength="255"/>
        <form:errors path="IL10Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL10Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL10Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL10Right" id="IL10Right"  maxlength="255"/>
        <form:errors path="IL10Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL10Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL10Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL10Top" id="IL10Top"  maxlength="255"/>
        <form:errors path="IL10Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL11Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL11Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL11Bottom" id="IL11Bottom"  maxlength="255"/>
        <form:errors path="IL11Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL11Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL11Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL11Left" id="IL11Left"  maxlength="255"/>
        <form:errors path="IL11Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL11Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL11Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL11Right" id="IL11Right"  maxlength="255"/>
        <form:errors path="IL11Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL11Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL11Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL11Top" id="IL11Top"  maxlength="255"/>
        <form:errors path="IL11Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL12Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL12Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL12Bottom" id="IL12Bottom"  maxlength="255"/>
        <form:errors path="IL12Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL12Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL12Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL12Left" id="IL12Left"  maxlength="255"/>
        <form:errors path="IL12Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL12Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL12Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL12Right" id="IL12Right"  maxlength="255"/>
        <form:errors path="IL12Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL12Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL12Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL12Top" id="IL12Top"  maxlength="255"/>
        <form:errors path="IL12Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL1Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL1Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL1Bottom" id="IL1Bottom"  maxlength="255"/>
        <form:errors path="IL1Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL1Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL1Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL1Left" id="IL1Left"  maxlength="255"/>
        <form:errors path="IL1Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL1Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL1Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL1Right" id="IL1Right"  maxlength="255"/>
        <form:errors path="IL1Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL1Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL1Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL1Top" id="IL1Top"  maxlength="255"/>
        <form:errors path="IL1Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL2Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL2Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL2Bottom" id="IL2Bottom"  maxlength="255"/>
        <form:errors path="IL2Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL2Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL2Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL2Left" id="IL2Left"  maxlength="255"/>
        <form:errors path="IL2Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL2Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL2Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL2Right" id="IL2Right"  maxlength="255"/>
        <form:errors path="IL2Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL2Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL2Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL2Top" id="IL2Top"  maxlength="255"/>
        <form:errors path="IL2Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL3Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL3Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL3Bottom" id="IL3Bottom"  maxlength="255"/>
        <form:errors path="IL3Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL3Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL3Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL3Left" id="IL3Left"  maxlength="255"/>
        <form:errors path="IL3Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL3Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL3Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL3Right" id="IL3Right"  maxlength="255"/>
        <form:errors path="IL3Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL3Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL3Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL3Top" id="IL3Top"  maxlength="255"/>
        <form:errors path="IL3Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL4Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL4Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL4Bottom" id="IL4Bottom"  maxlength="255"/>
        <form:errors path="IL4Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL4Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL4Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL4Left" id="IL4Left"  maxlength="255"/>
        <form:errors path="IL4Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL4Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL4Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL4Right" id="IL4Right"  maxlength="255"/>
        <form:errors path="IL4Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL4Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL4Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL4Top" id="IL4Top"  maxlength="255"/>
        <form:errors path="IL4Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL5Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL5Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL5Bottom" id="IL5Bottom"  maxlength="255"/>
        <form:errors path="IL5Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL5Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL5Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL5Left" id="IL5Left"  maxlength="255"/>
        <form:errors path="IL5Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL5Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL5Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL5Right" id="IL5Right"  maxlength="255"/>
        <form:errors path="IL5Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL5Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL5Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL5Top" id="IL5Top"  maxlength="255"/>
        <form:errors path="IL5Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL6Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL6Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL6Bottom" id="IL6Bottom"  maxlength="255"/>
        <form:errors path="IL6Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL6Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL6Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL6Left" id="IL6Left"  maxlength="255"/>
        <form:errors path="IL6Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL6Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL6Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL6Right" id="IL6Right"  maxlength="255"/>
        <form:errors path="IL6Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL6Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL6Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL6Top" id="IL6Top"  maxlength="255"/>
        <form:errors path="IL6Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL7Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL7Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL7Bottom" id="IL7Bottom"  maxlength="255"/>
        <form:errors path="IL7Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL7Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL7Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL7Left" id="IL7Left"  maxlength="255"/>
        <form:errors path="IL7Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL7Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL7Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL7Right" id="IL7Right"  maxlength="255"/>
        <form:errors path="IL7Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL7Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL7Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL7Top" id="IL7Top"  maxlength="255"/>
        <form:errors path="IL7Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL8Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL8Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL8Bottom" id="IL8Bottom"  maxlength="255"/>
        <form:errors path="IL8Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL8Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL8Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL8Left" id="IL8Left"  maxlength="255"/>
        <form:errors path="IL8Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL8Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL8Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL8Right" id="IL8Right"  maxlength="255"/>
        <form:errors path="IL8Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL8Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL8Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL8Top" id="IL8Top"  maxlength="255"/>
        <form:errors path="IL8Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL9Bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL9Bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL9Bottom" id="IL9Bottom"  maxlength="255"/>
        <form:errors path="IL9Bottom" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL9Left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL9Left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL9Left" id="IL9Left"  maxlength="255"/>
        <form:errors path="IL9Left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL9Right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL9Right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL9Right" id="IL9Right"  maxlength="255"/>
        <form:errors path="IL9Right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IL9Top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IL9Top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IL9Top" id="IL9Top"  maxlength="255"/>
        <form:errors path="IL9Top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.INoOfParition">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.INoOfParition" styleClass="control-label"/>
        <form:input cssClass="form-control" path="INoOfParition" id="INoOfParition"  maxlength="255"/>
        <form:errors path="INoOfParition" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IScreenHeight">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IScreenHeight" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IScreenHeight" id="IScreenHeight"  maxlength="255"/>
        <form:errors path="IScreenHeight" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.IScreenWidth">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.IScreenWidth" styleClass="control-label"/>
        <form:input cssClass="form-control" path="IScreenWidth" id="IScreenWidth"  maxlength="255"/>
        <form:errors path="IScreenWidth" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.ITextZone">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.ITextZone" styleClass="control-label"/>
        <form:input cssClass="form-control" path="ITextZone" id="ITextZone"  maxlength="255"/>
        <form:errors path="ITextZone" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.groupId">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.groupId" styleClass="control-label"/>
        <form:input cssClass="form-control" path="groupId" id="groupId"  maxlength="255"/>
        <form:errors path="groupId" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.image">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.image" styleClass="control-label"/>
        <form:input cssClass="form-control" path="image" id="image"  maxlength="255"/>
        <form:errors path="image" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.imageFile">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.imageFile" styleClass="control-label"/>
        <form:input cssClass="form-control" path="imageFile" id="imageFile"  maxlength="255"/>
        <form:errors path="imageFile" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.layoutDesc">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.layoutDesc" styleClass="control-label"/>
        <form:input cssClass="form-control" path="layoutDesc" id="layoutDesc"  maxlength="256"/>
        <form:errors path="layoutDesc" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.layoutName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.layoutName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="layoutName" id="layoutName"  maxlength="50"/>
        <form:errors path="layoutName" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.seq">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.seq" styleClass="control-label"/>
        <form:input cssClass="form-control" path="seq" id="seq"  maxlength="24"/>
        <form:errors path="seq" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost1">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost1" styleClass="control-label"/>
        <form:checkbox path="topMost1" id="topMost1" cssClass="checkbox"/>
        <form:errors path="topMost1" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost10">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost10" styleClass="control-label"/>
        <form:checkbox path="topMost10" id="topMost10" cssClass="checkbox"/>
        <form:errors path="topMost10" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost11">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost11" styleClass="control-label"/>
        <form:checkbox path="topMost11" id="topMost11" cssClass="checkbox"/>
        <form:errors path="topMost11" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost12">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost12" styleClass="control-label"/>
        <form:checkbox path="topMost12" id="topMost12" cssClass="checkbox"/>
        <form:errors path="topMost12" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost2">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost2" styleClass="control-label"/>
        <form:checkbox path="topMost2" id="topMost2" cssClass="checkbox"/>
        <form:errors path="topMost2" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost3">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost3" styleClass="control-label"/>
        <form:checkbox path="topMost3" id="topMost3" cssClass="checkbox"/>
        <form:errors path="topMost3" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost4">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost4" styleClass="control-label"/>
        <form:checkbox path="topMost4" id="topMost4" cssClass="checkbox"/>
        <form:errors path="topMost4" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost5">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost5" styleClass="control-label"/>
        <form:checkbox path="topMost5" id="topMost5" cssClass="checkbox"/>
        <form:errors path="topMost5" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost6">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost6" styleClass="control-label"/>
        <form:checkbox path="topMost6" id="topMost6" cssClass="checkbox"/>
        <form:errors path="topMost6" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost7">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost7" styleClass="control-label"/>
        <form:checkbox path="topMost7" id="topMost7" cssClass="checkbox"/>
        <form:errors path="topMost7" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost8">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost8" styleClass="control-label"/>
        <form:checkbox path="topMost8" id="topMost8" cssClass="checkbox"/>
        <form:errors path="topMost8" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutMst.topMost9">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutMst.topMost9" styleClass="control-label"/>
        <form:checkbox path="topMost9" id="topMost9" cssClass="checkbox"/>
        <form:errors path="topMost9" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty layoutMst.id}">
            <button type="submit" class="btn btn-danger" id="delete" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
            </button>
        </c:if>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="layoutMst" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['layoutMstForm']).focus();
    });
</script>
