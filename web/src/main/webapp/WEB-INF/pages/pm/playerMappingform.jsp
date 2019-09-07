<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="playerMappingDetail.title"/></title>
    <meta name="menu" content="PlayerMappingMenu"/>
    <meta name="heading" content="<fmt:message key='playerMappingDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="playerMappingList.playerMapping"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="playerMappingDetail.heading"/></h2>
    <fmt:message key="playerMappingDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="playerMapping" method="post" action="playerMappingform" cssClass="well"
           id="playerMappingForm" onsubmit="return validatePlayerMapping(this)">
<form:hidden path="id"/>
    <spring:bind path="playerMapping.createdBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerMapping.createdBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createdBy" id="createdBy"  maxlength="100"/>
        <form:errors path="createdBy" cssClass="help-block"/>
    </div>
    <spring:bind path="playerMapping.dateAdded">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerMapping.dateAdded" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateAdded" id="dateAdded"  maxlength="19"/>
        <form:errors path="dateAdded" cssClass="help-block"/>
    </div>
    <spring:bind path="playerMapping.dateLastModified">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerMapping.dateLastModified" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateLastModified" id="dateLastModified"  maxlength="19"/>
        <form:errors path="dateLastModified" cssClass="help-block"/>
    </div>
    <spring:bind path="playerMapping.lastModifiedBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerMapping.lastModifiedBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModifiedBy" id="lastModifiedBy"  maxlength="100"/>
        <form:errors path="lastModifiedBy" cssClass="help-block"/>
    </div>
    <spring:bind path="playerMapping.dateFrom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerMapping.dateFrom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateFrom" id="dateFrom" size="11" title="date" datepicker="true"/>
        <form:errors path="dateFrom" cssClass="help-block"/>
    </div>
    <spring:bind path="playerMapping.dateTo">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerMapping.dateTo" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateTo" id="dateTo" size="11" title="date" datepicker="true"/>
        <form:errors path="dateTo" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="player" items="playerList" itemLabel="label" itemValue="value"/>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="playerGroup" items="playerGroupList" itemLabel="label" itemValue="value"/>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty playerMapping.id}">
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

<v:javascript formName="playerMapping" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/webjars/bootstrap-datepicker/1.3.1/css/datepicker.css'/>" />
<script type="text/javascript" src="<c:url value='/webjars/bootstrap-datepicker/1.3.1/js/bootstrap-datepicker.js'/>"></script>
<c:if test="${pageContext.request.locale.language != 'en'}">
<script type="text/javascript" src="<c:url value='/webjars/bootstrap-datepicker/1.3.1/js/locales/bootstrap-datepicker.${pageContext.request.locale.language}.js'/>"></script>
</c:if>
<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['playerMappingForm']).focus();
        $('.text-right.date').datepicker({format: "<fmt:message key='calendar.format'/>", weekStart: "<fmt:message key='calendar.weekstart'/>", language: '${pageContext.request.locale.language}'});
    });
</script>
