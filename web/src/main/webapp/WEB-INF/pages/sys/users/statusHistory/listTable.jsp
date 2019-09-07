<%@ include file="/common/taglibs.jsp"%>

<table id="historyTable" class="sort-table table table-bordered table-hover" data-async="true">
    <thead>
    <tr>
        <th sort="id"><fmt:message key="user.id"/></th>
        <th sort="user.username"><fmt:message key="user.username"/></th>
        <th><fmt:message key="user.userstatus"/></th>
        <th sort="opDate"><fmt:message key="userStatusHistory.reason"/></th>
        <th sort="opUser.username"><fmt:message key="userStatusHistory.opuser"/></th>
        <th sort="opDate"><fmt:message key="userStatusHistory.opdate"/></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.content}" var="m">
        <tr>
            <td>${m.id}</td>
            <td><a href="${ctx}/admin/sys/users/${m.user.id}">${m.user.username}</a></td>
            <td>${m.status.info}</td>
            <td>${m.reason}</td>
            <td><a href="${ctx}/admin/sys/users/${m.opUser.id}">${m.opUser.username}</a></td>
            <td><spring:eval expression="m.opDate"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<sys:page page="${page}"/>

