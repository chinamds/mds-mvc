<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="playerTunerDetail.title"/></title>
    <meta name="menu" content="PlayerTunerMenu"/>
    <meta name="heading" content="<fmt:message key='playerTunerDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="playerTunerList.playerTuner"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="playerTunerDetail.heading"/></h2>
    <fmt:message key="playerTunerDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="playerTuner" method="post" action="playerTunerform" cssClass="well"
           id="playerTunerForm" onsubmit="return validatePlayerTuner(this)">
<form:hidden path="id"/>
    <spring:bind path="playerTuner.createdBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.createdBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createdBy" id="createdBy"  maxlength="100"/>
        <form:errors path="createdBy" cssClass="help-block"/>
    </div>
    <spring:bind path="playerTuner.dateAdded">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.dateAdded" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateAdded" id="dateAdded"  maxlength="19"/>
        <form:errors path="dateAdded" cssClass="help-block"/>
    </div>
    <spring:bind path="playerTuner.dateLastModified">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.dateLastModified" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateLastModified" id="dateLastModified"  maxlength="19"/>
        <form:errors path="dateLastModified" cssClass="help-block"/>
    </div>
    <spring:bind path="playerTuner.lastModifiedBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.lastModifiedBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModifiedBy" id="lastModifiedBy"  maxlength="100"/>
        <form:errors path="lastModifiedBy" cssClass="help-block"/>
    </div>
    <spring:bind path="playerTuner.channelName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.channelName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="channelName" id="channelName"  maxlength="50"/>
        <form:errors path="channelName" cssClass="help-block"/>
    </div>
    <spring:bind path="playerTuner.endTime">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.endTime" styleClass="control-label"/>
        <form:input cssClass="form-control" path="endTime" id="endTime"  maxlength="19"/>
        <form:errors path="endTime" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="mchannel" items="mchannelList" itemLabel="label" itemValue="value"/>
    <spring:bind path="playerTuner.output">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.output" styleClass="control-label"/>
        <form:input cssClass="form-control" path="output" id="output"  maxlength="255"/>
        <form:errors path="output" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="player" items="playerList" itemLabel="label" itemValue="value"/>
    <spring:bind path="playerTuner.startTime">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerTuner.startTime" styleClass="control-label"/>
        <form:input cssClass="form-control" path="startTime" id="startTime"  maxlength="19"/>
        <form:errors path="startTime" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty playerTuner.id}">
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

<v:javascript formName="playerTuner" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['playerTunerForm']).focus();
    });
</script>
