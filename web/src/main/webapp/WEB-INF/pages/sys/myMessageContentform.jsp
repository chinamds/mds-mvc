<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="myMessageContentDetail.title"/></title>
    <meta name="menu" content="MyMessageContentMenu"/>
    <meta name="heading" content="<fmt:message key='myMessageContentDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="myMessageContentList.myMessageContent"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="myMessageContentDetail.heading"/></h2>
    <fmt:message key="myMessageContentDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="myMessageContent" method="post" action="myMessageContentform" cssClass="well"
           id="myMessageContentForm" onsubmit="return validateMyMessageContent(this)">
<form:hidden path="id"/>
    <spring:bind path="myMessageContent.content">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="myMessageContent.content" styleClass="control-label"/>
        <form:input cssClass="form-control" path="content" id="content"  maxlength="255"/>
        <form:errors path="content" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="myMessage" items="myMessageList" itemLabel="label" itemValue="value"/>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty myMessageContent.id}">
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

<v:javascript formName="myMessageContent" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['myMessageContentForm']).focus();
    });
</script>
