<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="calendarDetail.title"/></title>
    <meta name="menu" content="CalendarMenu"/>
    <meta name="heading" content="<fmt:message key='calendarDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="calendarList.calendar"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="calendarDetail.heading"/></h2>
    <fmt:message key="calendarDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="calendar" method="post" action="calendarform" cssClass="well"
           id="calendarForm" onsubmit="return validateCalendar(this)">
<form:hidden path="id"/>
    <spring:bind path="calendar.approvalLevel">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.approvalLevel" styleClass="control-label"/>
        <form:input cssClass="form-control" path="approvalLevel" id="approvalLevel"  maxlength="255"/>
        <form:errors path="approvalLevel" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.approvalStatus">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.approvalStatus" styleClass="control-label"/>
        <form:input cssClass="form-control" path="approvalStatus" id="approvalStatus"  maxlength="255"/>
        <form:errors path="approvalStatus" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.arrEvent">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.arrEvent" styleClass="control-label"/>
        <form:input cssClass="form-control" path="arrEvent" id="arrEvent"  maxlength="255"/>
        <form:errors path="arrEvent" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="MChannel.id" id="MChannel.id">
    	<form:options items="${channelList}" itemValue="id" itemLabel="channelName"/>
     </form:select>
    <form:select cssClass="form-control" path="channel" items="channelList" itemLabel="label" itemValue="value"/>
    <spring:bind path="calendar.createDate">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.createDate" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createDate" id="createDate"  maxlength="19"/>
        <form:errors path="createDate" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.day">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.day" styleClass="control-label"/>
        <form:input cssClass="form-control" path="day" id="day"  maxlength="19"/>
        <form:errors path="day" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.event">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.event" styleClass="control-label"/>
        <form:input cssClass="form-control" path="event" id="event"  maxlength="50"/>
        <form:errors path="event" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.groupCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.groupCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="groupCode" id="groupCode"  maxlength="100"/>
        <form:errors path="groupCode" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.lastModify">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.lastModify" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModify" id="lastModify"  maxlength="19"/>
        <form:errors path="lastModify" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.playMeth">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.playMeth" styleClass="control-label"/>
        <form:input cssClass="form-control" path="playMeth" id="playMeth"  maxlength="255"/>
        <form:errors path="playMeth" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.remark">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.remark" styleClass="control-label"/>
        <form:input cssClass="form-control" path="remark" id="remark"  maxlength="255"/>
        <form:errors path="remark" cssClass="help-block"/>
    </div>
    <spring:bind path="calendar.userCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="calendar.userCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="userCode" id="userCode"  maxlength="100"/>
        <form:errors path="userCode" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty calendar.id}">
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

<v:javascript formName="calendar" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['calendarForm']).focus();
    });
</script>
