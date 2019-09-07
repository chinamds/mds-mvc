<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="bannerDetail.title"/></title>
    <meta name="menu" content="BannerMenu"/>
    <meta name="heading" content="<fmt:message key='bannerDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="bannerList.banner"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="bannerDetail.heading"/></h2>
    <fmt:message key="bannerDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="banner" method="post" action="bannerform" cssClass="well"
           id="bannerForm" onsubmit="return validateBanner(this)">
<form:hidden path="id"/>
    <spring:bind path="banner.HLColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.HLColor" styleClass="control-label"/>
        <form:checkbox path="HLColor" id="HLColor" cssClass="checkbox"/>
        <form:errors path="HLColor" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.XMLFormat">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.XMLFormat" styleClass="control-label"/>
        <form:input cssClass="form-control" path="XMLFormat" id="XMLFormat"  maxlength="1024"/>
        <form:errors path="XMLFormat" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.behavior">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.behavior" styleClass="control-label"/>
        <form:input cssClass="form-control" path="behavior" id="behavior"  maxlength="255"/>
        <form:errors path="behavior" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.bg">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.bg" styleClass="control-label"/>
        <form:input cssClass="form-control" path="bg" id="bg"  maxlength="255"/>
        <form:errors path="bg" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.bullet">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.bullet" styleClass="control-label"/>
        <form:input cssClass="form-control" path="bullet" id="bullet"  maxlength="255"/>
        <form:errors path="bullet" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.content">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.content" styleClass="control-label"/>
        <form:input cssClass="form-control" path="content" id="content"  maxlength="20"/>
        <form:errors path="content" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.customComments">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.customComments" styleClass="control-label"/>
        <form:input cssClass="form-control" path="customComments" id="customComments"  maxlength="255"/>
        <form:errors path="customComments" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.desc">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.desc" styleClass="control-label"/>
        <form:input cssClass="form-control" path="desc" id="desc"  maxlength="100"/>
        <form:errors path="desc" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.direction">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.direction" styleClass="control-label"/>
        <form:input cssClass="form-control" path="direction" id="direction"  maxlength="255"/>
        <form:errors path="direction" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.file">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.file" styleClass="control-label"/>
        <form:input cssClass="form-control" path="file" id="file"  maxlength="1024"/>
        <form:errors path="file" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.fontBold">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.fontBold" styleClass="control-label"/>
        <form:checkbox path="fontBold" id="fontBold" cssClass="checkbox"/>
        <form:errors path="fontBold" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.fontItalic">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.fontItalic" styleClass="control-label"/>
        <form:checkbox path="fontItalic" id="fontItalic" cssClass="checkbox"/>
        <form:errors path="fontItalic" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.fontUnderline">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.fontUnderline" styleClass="control-label"/>
        <form:checkbox path="fontUnderline" id="fontUnderline" cssClass="checkbox"/>
        <form:errors path="fontUnderline" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.halign">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.halign" styleClass="control-label"/>
        <form:input cssClass="form-control" path="halign" id="halign"  maxlength="20"/>
        <form:errors path="halign" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.indent">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.indent" styleClass="control-label"/>
        <form:input cssClass="form-control" path="indent" id="indent"  maxlength="255"/>
        <form:errors path="indent" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.language">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.language" styleClass="control-label"/>
        <form:input cssClass="form-control" path="language" id="language"  maxlength="20"/>
        <form:errors path="language" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.left">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.left" styleClass="control-label"/>
        <form:input cssClass="form-control" path="left" id="left"  maxlength="255"/>
        <form:errors path="left" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.scrollAmount">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.scrollAmount" styleClass="control-label"/>
        <form:input cssClass="form-control" path="scrollAmount" id="scrollAmount"  maxlength="255"/>
        <form:errors path="scrollAmount" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.speed">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.speed" styleClass="control-label"/>
        <form:input cssClass="form-control" path="speed" id="speed"  maxlength="255"/>
        <form:errors path="speed" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.strikethrough">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.strikethrough" styleClass="control-label"/>
        <form:checkbox path="strikethrough" id="strikethrough" cssClass="checkbox"/>
        <form:errors path="strikethrough" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.template">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.template" styleClass="control-label"/>
        <form:input cssClass="form-control" path="template" id="template"  maxlength="255"/>
        <form:errors path="template" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.textBKColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.textBKColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="textBKColor" id="textBKColor"  maxlength="255"/>
        <form:errors path="textBKColor" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.textFGColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.textFGColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="textFGColor" id="textFGColor"  maxlength="255"/>
        <form:errors path="textFGColor" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.textFontName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.textFontName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="textFontName" id="textFontName"  maxlength="50"/>
        <form:errors path="textFontName" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.textFontSize">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.textFontSize" styleClass="control-label"/>
        <form:input cssClass="form-control" path="textFontSize" id="textFontSize"  maxlength="255"/>
        <form:errors path="textFontSize" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.textHLColor">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.textHLColor" styleClass="control-label"/>
        <form:input cssClass="form-control" path="textHLColor" id="textHLColor"  maxlength="255"/>
        <form:errors path="textHLColor" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.top">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.top" styleClass="control-label"/>
        <form:input cssClass="form-control" path="top" id="top"  maxlength="255"/>
        <form:errors path="top" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.valign">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.valign" styleClass="control-label"/>
        <form:input cssClass="form-control" path="valign" id="valign"  maxlength="255"/>
        <form:errors path="valign" cssClass="help-block"/>
    </div>
    <spring:bind path="banner.valignString">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="banner.valignString" styleClass="control-label"/>
        <form:input cssClass="form-control" path="valignString" id="valignString"  maxlength="20"/>
        <form:errors path="valignString" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty banner.id}">
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

<v:javascript formName="banner" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
<script type="text/javascript" src="/static/scripts/script.jsp"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['bannerForm']).focus();
    });
</script>
