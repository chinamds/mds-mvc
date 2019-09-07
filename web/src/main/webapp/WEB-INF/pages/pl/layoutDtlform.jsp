<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="layoutDtlDetail.title"/></title>
    <meta name="menu" content="LayoutDtlMenu"/>
    <meta name="heading" content="<fmt:message key='layoutDtlDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="layoutDtlList.layoutDtl"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="layoutDtlDetail.heading"/></h2>
    <fmt:message key="layoutDtlDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="layoutDtl" method="post" action="layoutDtlform" cssClass="well"
           id="layoutDtlForm" onsubmit="return validateLayoutDtl(this)">
<form:hidden path="id"/>
    <spring:bind path="layoutDtl.createdBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.createdBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createdBy" id="createdBy"  maxlength="100"/>
        <form:errors path="createdBy" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.dateAdded">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.dateAdded" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateAdded" id="dateAdded"  maxlength="19"/>
        <form:errors path="dateAdded" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.dateLastModified">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.dateLastModified" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateLastModified" id="dateLastModified"  maxlength="19"/>
        <form:errors path="dateLastModified" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.lastModifiedBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.lastModifiedBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModifiedBy" id="lastModifiedBy"  maxlength="100"/>
        <form:errors path="lastModifiedBy" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.alpha">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.alpha" styleClass="control-label"/>
        <form:input cssClass="form-control" path="alpha" id="alpha"  maxlength="255"/>
        <form:errors path="alpha" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.bottom">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.bottom" styleClass="control-label"/>
        <form:input cssClass="form-control" path="bottom" id="bottom"  maxlength="255"/>
        <form:errors path="bottom" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="layoutMst" items="layoutMstList" itemLabel="label" itemValue="value"/>
    <spring:bind path="layoutDtl.left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="left" id="left"  maxlength="255"/>
        <form:errors path="left" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.level">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.level" styleClass="control-label"/>
        <form:input cssClass="form-control" path="level" id="level"  maxlength="255"/>
        <form:errors path="level" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.right">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.right" styleClass="control-label"/>
        <form:input cssClass="form-control" path="right" id="right"  maxlength="255"/>
        <form:errors path="right" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="top" id="top"  maxlength="255"/>
        <form:errors path="top" cssClass="help-block"/>
    </div>
    <spring:bind path="layoutDtl.zoneId">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="layoutDtl.zoneId" styleClass="control-label"/>
        <form:input cssClass="form-control" path="zoneId" id="zoneId"  maxlength="255"/>
        <form:errors path="zoneId" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty layoutDtl.id}">
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

<v:javascript formName="layoutDtl" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['layoutDtlForm']).focus();
    });
</script>
