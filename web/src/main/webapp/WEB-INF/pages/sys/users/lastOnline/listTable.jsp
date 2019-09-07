<%@ include file="/common/taglibs.jsp"%>

<table id="historyTable" class="sort-table table table-bordered table-hover" data-async="true">
    <thead>
    <tr>
        <th sort="username" style="width: 60px;"><fmt:message key="user.username"/></th>
        <th style="width: 100px"><fmt:message key="onlineUser.sessionid"/></th>
        <th style="width: 100px"><fmt:message key="onlineUser.userip"/></th>
        <th style="width: 100px"><fmt:message key="onlineUser.hostip"/></th>
        <th><fmt:message key="onlineUser.useragent"/></th>
        <th style="width: 90px"><fmt:message key="onlineUser.lastlogintime"/></th>
        <th style="width: 90px"><fmt:message key="onlineUser.lastlogouttime"/></th>
        <th style="width: 60px"><fmt:message key="onlineUser.logincount"/></th>
        <th style="width: 80px"><fmt:message key="onlineUser.onlineduration"/></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.content}" var="m">
        <tr>
            <td><a href="${ctx}/sys/users/${m.userId}">${m.username}</a></td>
            <td>${m.uid}</td>
            <td>${m.host}</td>
            <td>${m.systemHost}</td>
            <td>${m.userAgent}</td>
            <td><pretty:prettyTime date="${m.lastLoginTimestamp}"/></td>
            <td><pretty:prettyTime date="${m.lastStopTimestamp}"/></td>
            <td>${m.loginCount}</td>
            <td><pretty:prettySecond seconds="${m.totalOnlineTime}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<sys:page page="${page}"/>

