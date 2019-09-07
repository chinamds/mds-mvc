<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="myCalendarDetail.title"/></title>
    <meta name="menu" content="MyCalendarMenu"/>
    <meta name="heading" content="<fmt:message key='myCalendarDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="myCalendarList.myCalendar"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="myCalendarDetail.heading"/></h2>
    <fmt:message key="myCalendarDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="myCalendar" method="post" action="myCalendarform" cssClass="well"
           id="myCalendarForm" onsubmit="return validateMyCalendar(this)">
<form:hidden path="id"/>
    <spring:bind path="myCalendar.backgroundColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.backgroundColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="backgroundColor" id="backgroundColor"  maxlength="255"/>
        <form:errors path="backgroundColor" cssClass="help-block"/>
    </div>
    <spring:bind path="myCalendar.details">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.details" styleClass="control-label"/>
        <form:input cssClass="form-control" path="details" id="details"  maxlength="255"/>
        <form:errors path="details" cssClass="help-block"/>
    </div>
    <spring:bind path="myCalendar.duration">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.duration" styleClass="control-label"/>
        <form:input cssClass="form-control" path="duration" id="duration"  maxlength="255"/>
        <form:errors path="duration" cssClass="help-block"/>
    </div>
    <spring:bind path="myCalendar.endTime">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.endTime" styleClass="control-label"/>
        <form:input cssClass="form-control" path="endTime" id="endTime"  maxlength="255"/>
        <form:errors path="endTime" cssClass="help-block"/>
    </div>
    <spring:bind path="myCalendar.startDate">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.startDate" styleClass="control-label"/>
        <form:input cssClass="form-control" path="startDate" id="startDate" size="11" title="date" datepicker="true"/>
        <form:errors path="startDate" cssClass="help-block"/>
    </div>
    <spring:bind path="myCalendar.startTime">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.startTime" styleClass="control-label"/>
        <form:input cssClass="form-control" path="startTime" id="startTime"  maxlength="255"/>
        <form:errors path="startTime" cssClass="help-block"/>
    </div>
    <spring:bind path="myCalendar.textColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.textColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="textColor" id="textColor"  maxlength="255"/>
        <form:errors path="textColor" cssClass="help-block"/>
    </div>
    <spring:bind path="myCalendar.title">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myCalendar.title" styleClass="control-label"/>
        <form:input cssClass="form-control" path="title" id="title"  maxlength="255"/>
        <form:errors path="title" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
   <%--  <form:select cssClass="form-control" path="user" items="userList" itemLabel="label" itemValue="value"/> --%>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty myCalendar.id}">
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

<v:javascript formName="myCalendar" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<%-- <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/webjars/bootstrap-datepicker/1.3.1/css/datepicker.css'/>" />
<script type="text/javascript" src="<c:url value='/webjars/bootstrap-datepicker/1.3.1/js/bootstrap-datepicker.js'/>"></script>
<c:if test="${pageContext.request.locale.language != 'en'}">
<script type="text/javascript" src="<c:url value='/webjars/bootstrap-datepicker/1.3.1/js/locales/bootstrap-datepicker.${pageContext.request.locale.language}.js'/>"></script>
</c:if> --%>
<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['myCalendarForm']).focus();
        /* $('.text-right.date').datepicker({format: "<fmt:message key='calendar.format'/>", weekStart: "<fmt:message key='calendar.weekstart'/>", language: '${pageContext.request.locale.language}'}); */ 
    });
</script>
