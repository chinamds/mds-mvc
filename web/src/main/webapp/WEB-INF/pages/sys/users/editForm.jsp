<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="userProfile.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/editForm.js"%>
</c:set>

<div class="panel col-sm-6">
    <ul class="nav nav-tabs">
        <secure:hasPermission name="sys:user:add">
        <c:if test="${op eq 'add'}">
        <li ${op eq 'add' ? 'class="active"' : ''}>
            <a href="${ctx}/sys/user/create?BackURL=<sys:BackURL/>">
                <i class="icon-file-alt"></i>
                <fmt:message key="button.add"/>
            </a>
        </li>
        </c:if>
        </secure:hasPermission>

        <c:if test="${not empty m.id}">
        <li ${op eq 'view' ? 'class="active"' : ''}>
            <a href="${ctx}/sys/user/${m.id}?BackURL=<sys:BackURL/>">
                <i class="icon-eye-open"></i>
                <fmt:message key="button.view"/>
            </a>
        </li>
        <secure:hasPermission name="sys:user:edit">
        <li ${op eq 'edit' ? 'class="active"' : ''}>
            <a href="${ctx}/sys/user/${m.id}/update?BackURL=<sys:BackURL/>">
                <i class="fa fa-edit"></i>
                <fmt:message key="button.edit"/>
            </a>
        </li>
        </secure:hasPermission>
        <secure:hasPermission name="sys:user:delete">
        <li ${op eq 'delete' ? 'class="active"' : ''}>
            <a href="${ctx}/sys/user/${m.id}/delete?BackURL=<sys:BackURL/>">
                <i class="fa fa-trash"></i>
                <fmt:message key="button.delete"/>
            </a>
        </li>
        </secure:hasPermission>
        </c:if>
        <li>
            <a href="<sys:BackURL/>" class="btn btn-link">
                <i class="icon-reply"></i>
                <fmt:message key="button.done"/>
            </a>
        </li>
    </ul>

    <form:form id="editForm" method="post" modelAttribute="m" cssClass="form-horizontal">
        <sys:showGlobalError modelAttribute="m"/>
        <form:hidden path="id"/>
        <form:hidden path="deleted"/>
        <form:hidden path="passwordHint"/>
        <div id="baseinfo">
            <h4 class="hr"><fmt:message key="userProfile.title"/></h4>
            <div class="control-group span4">
                <form:label path="username" cssClass="control-label"><fmt:message key="user.username"/></form:label>
                <div class="controls">
                    <form:input path="username" cssClass="validate[required,custom[username],ajax[ajaxCall]]"
                                placeholder="<fmt:message key="user.usernametip"/>"/>
                </div>
            </div>

            <div class="control-group span4">
                <form:label path="email" cssClass="control-label"><fmt:message key="user.email"/></form:label>
                <div class="controls">
                    <form:input path="email" cssClass="validate[required,custom[email],ajax[ajaxCall]]"
                                placeholder="<fmt:message key="user.emailtip"/>"/>
                </div>
            </div>
            <div class="control-group span4">
                <form:label path="mobilePhoneNumber" cssClass="control-label"><fmt:message key="user.mobile"/></form:label>
                <div class="controls">
                    <form:input path="mobilePhoneNumber"
                                cssClass="validate[required,custom[mobilePhoneNumber],ajax[ajaxCall]]"
                                placeholder="<fmt:message key="user.mobiletip"/>"/>
                </div>
            </div>

            <div class="control-group span4">
                <form:label path="createDate" cssClass="control-label"><fmt:message key="user.dateAdded"/></form:label>
                <div class="controls input-append date">
                    <form:input path="createDate"
                                  data-format="yyyy-MM-dd hh:mm:ss"
                                  data-position="bottom-left"
                                  placeholder="<fmt:message key="user.dateAddedtip"/>"/>
                    <span class="add-on"><i data-time-icon="icon-time" data-date-icon="icon-calendar"></i></span>
                </div>
            </div>

            <c:choose>
                <c:when test="${op eq 'add'}">
                    <div class="control-group span4">
                        <form:label path="password" cssClass="control-label"><fmt:message key="user.password"/></form:label>
                        <div class="controls">
                            <form:password path="password" cssClass="validate[required,minSize[5],maxSize[100]]"
                                             placeholder="<fmt:message key="user.passwordtip"/>"/>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <form:hidden path="password"/>
                </c:otherwise>
            </c:choose>

            <div class="clearfix"></div>

            <div class="control-group span4">
                <form:label path="admin" cssClass="control-label"><fmt:message key="user.usertype"/></form:label>
                <div class="controls inline-radio">
                    <form:radiobuttons path="admin" items="${booleanList}" itemLabel="info" itemValue="value" cssClass="validate[required]"/>
                </div>
            </div>

            <div class="control-group span4">
                <form:label path="status" cssClass="control-label"><fmt:message key="user.userstatus"/></form:label>
                <div class="controls inline-radio">
                    <form:radiobuttons path="status" items="${statusList}" itemLabel="info" cssClass="validate[required]"/>
                </div>
            </div>
        </div>

        <c:if test="${op eq 'add'}">
            <c:set var="icon" value="icon-file-alt"/>
        </c:if>
        <c:if test="${op eq 'edit'}">
            <c:set var="icon" value="fa fa-edit"/>
        </c:if>
        <c:if test="${op eq 'delete'}">
            <c:set var="icon" value="fa fa-trash"/>
        </c:if>

        <div class="control-group left-group">
            <div>
                <button type="submit" class="btn btn-primary">
                    <i class="${icon}"></i>
                        ${op}
                </button>
                <a href="<sys:BackURL/>" class="btn">
                    <i class="icon-reply"></i>
                    <fmt:message key="button.done"/>
                </a>
            </div>
        </div>

    </form:form>
</div>