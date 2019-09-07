<%@ include file="/common/taglibs.jsp"%>

<div class="scroll-pane">
    <table id="table" class="sort-table table table-bordered table-hover"
            data-async="true" data-async-callback="callback" data-async-container="panel" style="width:1000px;max-width: 1000px;">
        <thead>
        <tr>
            <th style="width: 80px;">
                <a class="check-all" href="javascript:;"><fmt:message key="button.selectall"/></a>
                |
                <a class="reverse-all" href="javascript:;"><fmt:message key="button.invertselection"/></a>
            </th>
            <th sort="username" style="width: 60px;"><fmt:message key="user.username"/></th>
            <th style="width: 100px"><fmt:message key="onlineUser.userip"/></th>
            <th style="width: 100px"><fmt:message key="onlineUser.hostip"/></th>
            <th style="width: 90px"><fmt:message key="onlineUser.logintime"/></th>
            <th style="width: 90px"><fmt:message key="onlineUser.lastaccesstime"/></th>
            <th style="width: 50px"><fmt:message key="onlineUser.status"/></th>
            <th><fmt:message key="onlineUser.useragent"/></th>
            <th><fmt:message key="onlineUser.sessionid"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.content}" var="m">
            <tr>
                <td class="check">
                    <input type="checkbox" name="ids" value="${m.id}"/>
                </td>
                <td>
                    <c:if test="${m.userId eq 0}"><fmt:message key="onlineUser.guest"/></c:if>
                    <a href="${ctx}/admin/sys/users/${m.userId}">${m.username}</a>
                </td>
                <td>${m.host}</td>
                <td>${m.systemHost}</td>
                <td><pretty:prettyTime date="${m.startTimestamp}"/></td>
                <td><pretty:prettyTime date="${m.lastAccessTime}"/></td>
                <td>${m.status.info}</td>
                <td>${m.userAgent}</td>
                <td>${m.id}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<sys:page page="${page}"/>
