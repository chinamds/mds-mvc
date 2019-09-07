<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="playerDetail.title"/></title>
    <meta name="menu" content="PlayerMenu"/>
    <meta name="heading" content="<fmt:message key='playerDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="playerList.player"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="playerDetail.heading"/></h2>
    <fmt:message key="playerDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="player" method="post" action="playerform" cssClass="well"
           id="playerForm" onsubmit="return validatePlayer(this)">
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
<form:hidden path="DCMVersion"/>
<form:hidden path="MACAddress"/>
<form:hidden path="MACAddress1"/>
<form:hidden path="MACID"/>
<form:hidden path="beforeDay"/>
<form:hidden path="binary"/>
<form:hidden path="connectionTimeout"/>
<form:hidden path="connectionType"/>
<form:hidden path="dbLimit"/>
<form:hidden path="deviceID"/>
<form:hidden path="diskSerial"/>
<form:hidden path="ftpContent"/>
<form:hidden path="ftpPeriod"/>
<form:hidden path="guidReg"/>
<form:hidden path="lastOnlineTime"/>
<form:hidden path="lastSyncTime"/>
<form:hidden path="localLogin"/>
<form:hidden path="localPassword"/>
<form:hidden path="localPort"/>
<form:hidden path="login"/>
<form:hidden path="online"/>
<form:hidden path="password"/>
<form:hidden path="phoneNumber"/>
<form:hidden path="phoneNumberServer"/>
<form:hidden path="port"/>
<form:hidden path="proxyServer"/>
<form:hidden path="readBufferSize"/>
<form:hidden path="regTime"/>
<form:hidden path="replaceFile"/>
<form:hidden path="retries"/>
<form:hidden path="retryDelay"/>
<form:hidden path="useFirewall"/>
<form:hidden path="usePASVMode"/>
<form:hidden path="userAgent"/>
<form:hidden path="serverAddress"/>
<form:hidden path="shutdown"/>
<form:hidden path="startup"/>
<form:hidden path="timeOuts"/>
<form:hidden path="localAddress"/>

	<spring:bind path="player.organization">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="player.organization" styleClass="control-label"/>
		<input type="hidden" name="organizationId" id="organizationId" value="${player.organization.id}"/>
		<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${player.organization.id}" fieldName="organizationName" fieldValue="${player.organization.fullName}"
			url="/sys/organizations/treeData" extId="${player.organization.id}" cssClass="required form-control" maxlength="200"/>
		<form:errors path="organization" cssClass="help-block"/>
	</div>
			
	<spring:bind path="player.uniqueName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="player.uniqueName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="uniqueName" id="uniqueName"  maxlength="50"/>
        <form:errors path="uniqueName" cssClass="help-block"/>
    </div>
	<spring:bind path="player.playerName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="player.playerName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="playerName" id="playerName"  maxlength="100"/>
        <form:errors path="playerName" cssClass="help-block"/>
    </div>
    <spring:bind path="player.publicIP">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="player.publicIP" styleClass="control-label"/>
        <form:input cssClass="form-control" path="publicIP" id="publicIP"  maxlength="20"/>
        <form:errors path="publicIP" cssClass="help-block"/>
    </div>
    <spring:bind path="player.location">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="player.location" styleClass="control-label"/>
        <form:input cssClass="form-control" path="location" id="location"  maxlength="255"/>
        <form:errors path="location" cssClass="help-block"/>
    </div>
    <spring:bind path="player.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="player.description" styleClass="control-label"/>
        <form:input cssClass="form-control" path="description" id="description"  maxlength="255"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>   

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty player.id}">
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

<v:javascript formName="player" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['playerForm']).focus();
    });
</script>
