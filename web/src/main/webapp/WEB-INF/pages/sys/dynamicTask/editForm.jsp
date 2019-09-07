<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="taskDefinitionDetail.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/dynamicTask/editForm.js"%>
</c:set>

<div class="panel">

    <ul class="nav nav-tabs">
        <secure:hasPermission name="maintain:dynamicTask:create">
        <c:if test="${op eq 'add'}">
            <li ${op eq 'add' ? 'class="active"' : ''}>
                <a href="${ctx}/admin/maintain/dynamicTask/create?BackURL=<es:BackURL/>">
                    <i class="icon-file-alt"></i>
                    <fmt:message key="button.add"/>
                </a>
            </li>
        </c:if>
        </secure:hasPermission>


        <c:if test="${not empty m.id}">
            <li ${op eq 'view' ? 'class="active"' : ''}>
                <a href="${ctx}/admin/maintain/dynamicTask/${m.id}?BackURL=<es:BackURL/>">
                    <i class="icon-eye-open"></i>
                    <fmt:message key="button.view"/>
                </a>
            </li>
            <secure:hasPermission name="maintain:dynamicTask:update">
            <li ${op eq 'edit' ? 'class="active"' : ''}>
                <a href="${ctx}/admin/maintain/dynamicTask/${m.id}/update?BackURL=<es:BackURL/>">
                    <i class="fa fa-edit"></i>
                    <fmt:message key="button.edit"/>
                </a>
            </li>
            </secure:hasPermission>

            <secure:hasPermission name="maintain:dynamicTask:delete">
            <li ${op eq 'delete' ? 'class="active"' : ''}>
                <a href="${ctx}/admin/maintain/dynamicTask/${m.id}/delete?BackURL=<es:BackURL/>">
                    <i class="fa fa-trash"></i>
                    <fmt:message key="button.delete"/>
                </a>
            </li>
            </secure:hasPermission>
        </c:if>
        <li>
            <a href="<es:BackURL/>" class="btn btn-link">
                <i class="icon-reply"></i>
                <fmt:message key="button.done"/>
            </a>
        </li>
    </ul>

    <form:form id="editForm" method="post" modelAttribute="m" cssClass="form-horizontal">
        <!--上一个地址 如果提交方式是get 需要加上-->
        <%--<es:BackURL hiddenInput="true"/>--%>

            <sys:showGlobalError modelAttribute="m"/>

            <form:hidden path="id"/>
            <form:hidden path="start"/>
            <form:hidden path="description"/>

            <div class="control-group">
                <form:label path="name" cssClass="control-label">任务名称</form:label>
                <div class="controls">
                    <form:input path="name" cssClass="validate[required]"/>
                </div>
            </div>

            <div class="control-group">
                <form:label path="cron" cssClass="control-label">cron表达式</form:label>
                <div class="controls">
                    <form:input path="cron" cssClass="validate[required]" placeholder="如 0 30 2 * * ?"/>
                </div>
            </div>

            <div class="control-group">
                <form:label path="beanName" cssClass="control-label">任务Bean名称</form:label>
                <div class="controls">
                    <form:input path="beanName" placeholder="Spring bean名字"/>
                </div>
            </div>
            <div class="control-group">
                <form:label path="beanClass" cssClass="control-label">任务全限定类名</form:label>
                <div class="controls">
                    <form:input path="beanClass" placeholder="任务类全限定类名"/>
                </div>
            </div>

            <div class="control-group">
                <form:label path="methodName" cssClass="control-label">任务方法名</form:label>
                <div class="controls">
                    <form:input path="methodName" cssClass="validate[required]" placeholder="任务方法名"/>
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

            <div class="control-group">
                <div class="controls">
                    <button type="submit" class="btn btn-primary">
                        <i class="${icon}"></i>
                            ${op}
                    </button>
                    <a href="<es:BackURL/>" class="btn">
                        <i class="icon-reply"></i>
                       <fmt:message key="button.done"/>
                    </a>
                </div>
            </div>


    </form:form>
</div>
