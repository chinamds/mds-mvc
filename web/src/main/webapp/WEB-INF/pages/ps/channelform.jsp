<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="channelDetail.title"/></title>
    <meta name="menu" content="ChannelMenu"/>
    <meta name="heading" content="<fmt:message key='channelDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="channelList.channel"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="channelDetail.heading"/></h2>
    <fmt:message key="channelDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="channel" method="post" action="channelform" cssClass="well"
           id="channelForm" onsubmit="return validateChannel(this)">
<form:hidden path="id"/>
    <spring:bind path="channel.BAllContent">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.BAllContent" styleClass="control-label"/>
        <form:input cssClass="form-control" path="BAllContent" id="BAllContent"  maxlength="255"/>
        <form:errors path="BAllContent" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.BImm">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.BImm" styleClass="control-label"/>
        <form:input cssClass="form-control" path="BImm" id="BImm"  maxlength="255"/>
        <form:errors path="BImm" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.BIncludeToday">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.BIncludeToday" styleClass="control-label"/>
        <form:input cssClass="form-control" path="BIncludeToday" id="BIncludeToday"  maxlength="255"/>
        <form:errors path="BIncludeToday" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.channelDesc">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.channelDesc" styleClass="control-label"/>
        <form:input cssClass="form-control" path="channelDesc" id="channelDesc"  maxlength="100"/>
        <form:errors path="channelDesc" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.channelName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.channelName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="channelName" id="channelName"  maxlength="50"/>
        <form:errors path="channelName" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.createDate">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.createDate" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createDate" id="createDate"  maxlength="19"/>
        <form:errors path="createDate" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.defPlaylist">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.defPlaylist" styleClass="control-label"/>
        <form:input cssClass="form-control" path="defPlaylist" id="defPlaylist"  maxlength="50"/>
        <form:errors path="defPlaylist" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.ftpTime">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.ftpTime" styleClass="control-label"/>
        <form:input cssClass="form-control" path="ftpTime" id="ftpTime"  maxlength="5"/>
        <form:errors path="ftpTime" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.groupCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.groupCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="groupCode" id="groupCode"  maxlength="100"/>
        <form:errors path="groupCode" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.lastModify">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.lastModify" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModify" id="lastModify"  maxlength="19"/>
        <form:errors path="lastModify" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.period">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.period" styleClass="control-label"/>
        <form:input cssClass="form-control" path="period" id="period"  maxlength="255"/>
        <form:errors path="period" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.timeOuts">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.timeOuts" styleClass="control-label"/>
        <form:input cssClass="form-control" path="timeOuts" id="timeOuts"  maxlength="5"/>
        <form:errors path="timeOuts" cssClass="help-block"/>
    </div>
    <spring:bind path="channel.userCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="channel.userCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="userCode" id="userCode"  maxlength="100"/>
        <form:errors path="userCode" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty channel.id}">
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

<v:javascript formName="channel" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['channelForm']).focus();
    });
</script>
