<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="catalogueDetail.title"/></title>
    <meta name="menu" content="CatalogueMenu"/>
    <meta name="heading" content="<fmt:message key='catalogueDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="catalogueList.catalogue"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="catalogueDetail.heading"/></h2>
    <fmt:message key="catalogueDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="catalogue" method="post" action="catalogueform" cssClass="well"
           id="catalogueForm" onsubmit="return validateCatalogue(this)">
<form:hidden path="id"/>
    <spring:bind path="catalogue.BGColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.BGColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="BGColor" id="BGColor"  maxlength="255"/>
        <form:errors path="BGColor" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.BGMusic">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.BGMusic" styleClass="control-label"/>
        <form:checkbox path="BGMusic" id="BGMusic" cssClass="checkbox"/>
        <form:errors path="BGMusic" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.bgType">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.bgType" styleClass="control-label"/>
        <form:input cssClass="form-control" path="bgType" id="bgType"  maxlength="255"/>
        <form:errors path="bgType" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.btnAlign">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.btnAlign" styleClass="control-label"/>
        <form:input cssClass="form-control" path="btnAlign" id="btnAlign"  maxlength="255"/>
        <form:errors path="btnAlign" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.btnLng">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.btnLng" styleClass="control-label"/>
        <form:input cssClass="form-control" path="btnLng" id="btnLng"  maxlength="3"/>
        <form:errors path="btnLng" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.btnStyle">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.btnStyle" styleClass="control-label"/>
        <form:input cssClass="form-control" path="btnStyle" id="btnStyle"  maxlength="255"/>
        <form:errors path="btnStyle" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.catalogueDesc">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.catalogueDesc" styleClass="control-label"/>
        <form:input cssClass="form-control" path="catalogueDesc" id="catalogueDesc"  maxlength="100"/>
        <form:errors path="catalogueDesc" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.catalogueName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.catalogueName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="catalogueName" id="catalogueName"  maxlength="20"/>
        <form:errors path="catalogueName" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.createDate">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.createDate" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createDate" id="createDate"  maxlength="19"/>
        <form:errors path="createDate" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.fontBold">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.fontBold" styleClass="control-label"/>
        <form:checkbox path="fontBold" id="fontBold" cssClass="checkbox"/>
        <form:errors path="fontBold" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.fontColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.fontColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="fontColor" id="fontColor"  maxlength="255"/>
        <form:errors path="fontColor" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.fontItalic">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.fontItalic" styleClass="control-label"/>
        <form:checkbox path="fontItalic" id="fontItalic" cssClass="checkbox"/>
        <form:errors path="fontItalic" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.fontName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.fontName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="fontName" id="fontName"  maxlength="50"/>
        <form:errors path="fontName" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.fontSize">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.fontSize" styleClass="control-label"/>
        <form:input cssClass="form-control" path="fontSize" id="fontSize"  maxlength="255"/>
        <form:errors path="fontSize" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.fontUnderline">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.fontUnderline" styleClass="control-label"/>
        <form:checkbox path="fontUnderline" id="fontUnderline" cssClass="checkbox"/>
        <form:errors path="fontUnderline" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.groupCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.groupCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="groupCode" id="groupCode"  maxlength="100"/>
        <form:errors path="groupCode" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.imageFile">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.imageFile" styleClass="control-label"/>
        <form:input cssClass="form-control" path="imageFile" id="imageFile"  maxlength="1024"/>
        <form:errors path="imageFile" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.interactive">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.interactive" styleClass="control-label"/>
        <form:checkbox path="interactive" id="interactive" cssClass="checkbox"/>
        <form:errors path="interactive" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.lastModify">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.lastModify" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModify" id="lastModify"  maxlength="19"/>
        <form:errors path="lastModify" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.layoutName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.layoutName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="layoutName" id="layoutName"  maxlength="50"/>
        <form:errors path="layoutName" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.musicFile">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.musicFile" styleClass="control-label"/>
        <form:input cssClass="form-control" path="musicFile" id="musicFile"  maxlength="1024"/>
        <form:errors path="musicFile" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.quantity">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.quantity" styleClass="control-label"/>
        <form:input cssClass="form-control" path="quantity" id="quantity"  maxlength="255"/>
        <form:errors path="quantity" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.screenType">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.screenType" styleClass="control-label"/>
        <form:input cssClass="form-control" path="screenType" id="screenType"  maxlength="255"/>
        <form:errors path="screenType" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.skin">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.skin" styleClass="control-label"/>
        <form:input cssClass="form-control" path="skin" id="skin"  maxlength="255"/>
        <form:errors path="skin" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.skinCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.skinCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="skinCode" id="skinCode"  maxlength="25"/>
        <form:errors path="skinCode" cssClass="help-block"/>
    </div>
    <spring:bind path="catalogue.userCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="catalogue.userCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="userCode" id="userCode"  maxlength="100"/>
        <form:errors path="userCode" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty catalogue.id}">
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

<v:javascript formName="catalogue" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['catalogueForm']).focus();
    });
</script>
