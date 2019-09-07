<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="myMessageRecipientDetail.title"/></title>
    <meta name="menu" content="MyMessageRecipientMenu"/>
    <meta name="heading" content="<fmt:message key='myMessageRecipientDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="myMessageRecipientList.myMessageRecipient"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="myMessageRecipientDetail.heading"/></h2>
    <fmt:message key="myMessageRecipientDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="myMessageRecipient" method="post" action="myMessageRecipientform" cssClass="well"
           id="myMessageRecipientForm" onsubmit="return validateMyMessageRecipient(this)">
<form:hidden path="id"/>
    <spring:bind path="myMessageRecipient.messageState">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myMessageRecipient.messageState" styleClass="control-label"/>
        <form:input cssClass="form-control" path="messageState" id="messageState"  maxlength="255"/>
        <form:errors path="messageState" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="myMessage" items="myMessageList" itemLabel="label" itemValue="value"/>
    <spring:bind path="myMessageRecipient.recievedTime">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myMessageRecipient.recievedTime" styleClass="control-label"/>
        <form:input cssClass="form-control" path="recievedTime" id="recievedTime"  maxlength="255"/>
        <form:errors path="recievedTime" cssClass="help-block"/>
    </div>
    <spring:bind path="myMessageRecipient.recipientType">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myMessageRecipient.recipientType" styleClass="control-label"/>
        <form:input cssClass="form-control" path="recipientType" id="recipientType"  maxlength="255"/>
        <form:errors path="recipientType" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="user" items="userList" itemLabel="label" itemValue="value"/>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty myMessageRecipient.id}">
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

<v:javascript formName="myMessageRecipient" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['myMessageRecipientForm']).focus();
    });
</script>
