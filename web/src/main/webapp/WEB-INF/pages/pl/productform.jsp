<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="productDetail.title"/></title>
    <meta name="menu" content="ProductMenu"/>
    <meta name="heading" content="<fmt:message key='productDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="productList.product"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="productDetail.heading"/></h2>
    <fmt:message key="productDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="product" method="post" action="productform" cssClass="well"
           id="productForm" onsubmit="return validateProduct(this)">
<form:hidden path="id"/>
    <spring:bind path="product.arrProductName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="product.arrProductName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="arrProductName" id="arrProductName"  maxlength="255"/>
        <form:errors path="arrProductName" cssClass="help-block"/>
    </div>
    <spring:bind path="product.btnEvent">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="product.btnEvent" styleClass="control-label"/>
        <form:input cssClass="form-control" path="btnEvent" id="btnEvent"  maxlength="20"/>
        <form:errors path="btnEvent" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="catalogue.id" id="catalogue.id">
    	<form:options items="${catalogueList}" itemValue="id" itemLabel="catalogueName"/>
     </form:select>
    <spring:bind path="product.imageFile">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="product.imageFile" styleClass="control-label"/>
        <form:input cssClass="form-control" path="imageFile" id="imageFile"  maxlength="1024"/>
        <form:errors path="imageFile" cssClass="help-block"/>
    </div>
    <spring:bind path="product.language">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="product.language" styleClass="control-label"/>
        <form:input cssClass="form-control" path="language" id="language"  maxlength="255"/>
        <form:errors path="language" cssClass="help-block"/>
    </div>
    <spring:bind path="product.productDesc">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="product.productDesc" styleClass="control-label"/>
        <form:input cssClass="form-control" path="productDesc" id="productDesc"  maxlength="50"/>
        <form:errors path="productDesc" cssClass="help-block"/>
    </div>
    <spring:bind path="product.productIndex">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="product.productIndex" styleClass="control-label"/>
        <form:input cssClass="form-control" path="productIndex" id="productIndex"  maxlength="255"/>
        <form:errors path="productIndex" cssClass="help-block"/>
    </div>
    <spring:bind path="product.productName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="product.productName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="productName" id="productName"  maxlength="20"/>
        <form:errors path="productName" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty product.id}">
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

<v:javascript formName="product" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['productForm']).focus();
    });
</script>
