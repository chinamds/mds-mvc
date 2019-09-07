<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="galleryMappingDetail.title"/></title>
    <meta name="menu" content="GalleryMappingMenu"/>
    <meta name="heading" content="<fmt:message key='galleryMappingDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="galleryMappingList.galleryMapping"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="galleryMappingDetail.heading"/></h2>
    <fmt:message key="galleryMappingDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="galleryMapping" method="post" action="galleryMappingform" cssClass="well"
           id="galleryMappingForm" onsubmit="return validateGalleryMapping(this)">
<form:hidden path="id"/>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="area" items="areaList" itemLabel="label" itemValue="value"/>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="company" items="companyList" itemLabel="label" itemValue="value"/>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="department" items="departmentList" itemLabel="label" itemValue="value"/>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="gallery" items="galleryList" itemLabel="label" itemValue="value"/>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="user" items="userList" itemLabel="label" itemValue="value"/>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty galleryMapping.id}">
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

<v:javascript formName="galleryMapping" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['galleryMappingForm']).focus();
    });
</script>
