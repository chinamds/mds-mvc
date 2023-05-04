<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="uiTemplateDetail.title"/></title>
    <meta name="decorator" content="default_panel"/>
    <meta name="menu" content="UiTemplateMenu"/>
    <meta name="heading" content="<fmt:message key='uiTemplateDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/uiTemplateform.js"%>
</c:set>

<%-- <c:set var="delObject" scope="request"><fmt:message key="uiTemplateList.uiTemplate"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script> --%>

<body class="mds-bootstrap-dialog">
<div class="col-sm-3">
    <h2><fmt:message key="uiTemplateDetail.heading"/></h2>
    <fmt:message key="uiTemplateDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="uiTemplate" method="post" action="uiTemplateform" cssClass="well"
           id="uiTemplateForm" onsubmit="return validateUiTemplate(this)" enctype="multipart/form-data">      
<form:hidden path="id"/>

    <div class="mb-3">
        <appfuse:label key="uiTemplate.gallery" styleClass="control-label"/>
        <spring:bind path="uiTemplate.gallery">
        <select class="form-control${(not empty status.errorMessage) ? ' is-invalid' : ''}" id="gallery" name="gallery">
        </spring:bind>
            <option selected="selected" value="${uiTemplate.gallery.id}">${uiTemplate.gallery.name}</option>
        </select>
        <form:errors path="gallery" cssClass="invalid-feedback help-block"/>
	</div>
    <div class="mb-3">
        <appfuse:label key="uiTemplate.name" styleClass="control-label"/>
        <spring:bind path="uiTemplate.name">
        <form:input cssClass="form-control${(not empty status.errorMessage) ? ' is-invalid' : ''}" path="name" id="name"  maxlength="100"/>
        </spring:bind>
        <form:errors path="name" cssClass="invalid-feedback help-block"/>
    </div>
    <div class="mb-3">
        <appfuse:label key="uiTemplate.templateType" styleClass="control-label"/>
        <spring:bind path="uiTemplate.templateType">
        <select id="templateType" name="templateType" class="form-select${(not empty status.errorMessage) ? ' is-invalid' : ''}">
            <c:forEach items="${templateTypes}" var="fType">
            <option value="${fType.templateType}" ${ fType.templateType eq uiTemplate.templateType ? 'selected' : ''}>${fType.info}</option>
            </c:forEach>
        </select>
        </spring:bind>
        <form:errors path="templateType" cssClass="invalid-feedback help-block"/>
    </div>
    <div class="mb-3">
        <appfuse:label key="uiTemplate.htmlTemplate" styleClass="control-label"/>
        <spring:bind path="uiTemplate.htmlTemplate">
         <form:textarea path="htmlTemplate" id="htmlTemplate" rows='8' cssClass="form-control${(not empty status.errorMessage) ? ' is-invalid' : ''}"/>
         </spring:bind>
        <form:errors path="htmlTemplate" cssClass="invalid-feedback help-block"/>
    </div>
    <div class="mb-3">
        <appfuse:label key="uiTemplate.scriptTemplate" styleClass="control-label"/>
        <spring:bind path="uiTemplate.scriptTemplate">
         <form:textarea path="scriptTemplate" id="scriptTemplate" rows='3' cssClass="form-control${(not empty status.errorMessage) ? ' is-invalid' : ''}"/>
         </spring:bind>
        <form:errors path="scriptTemplate" cssClass="invalid-feedback help-block"/>
    </div>
    <div class="mb-3">
        <appfuse:label key="uiTemplate.description" styleClass="control-label"/>
        <spring:bind path="uiTemplate.description">
         <form:textarea path="description" id="description" rows='2' cssClass="form-control${(not empty status.errorMessage) ? ' is-invalid' : ''}"/>
         </spring:bind>
        <form:errors path="description" cssClass="invalid-feedback help-block"/>
    </div>

    <div class="mb-3">
    	<secure:hasAnyPermissions name="cm:uiTemplates:add,cm:uiTemplates:edit">
        <button type="button" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>
        <secure:hasPermission name="cm:uiTemplates:delete">
        <c:if test="${not empty uiTemplate.id}">
            <button type="button" class="btn btn-danger" id="delete" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
            </button>
        </c:if>
        </secure:hasPermission>

        <button type="button" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="uiTemplate" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
</body>
