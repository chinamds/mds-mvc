<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="userList.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/list.js"%>
</c:set>

<c:set var="organizationId" value="${empty organization ? 0 : organization.id}"/>
<c:set var="jobId" value="${empty job ? 0 : job.id}"/>

<div data-table="table" class="panel">
    <ul class="nav nav-tabs">
        <li ${param['search.deleted_eq'] ne 'true' and param['search.status_eq'] ne 'blocked' ? 'class="active"' : ''}>
            <a href="${ctx}/sys/users/${organizationId}/${jobId}">
                <i class="fa fa-table"></i>
                <fmt:message key="userList.title"/>
            </a>
        </li>
        <li ${param['search.deleted_eq'] eq 'true' ? 'class="active"' : ''}>
            <a href="${ctx}/sys/users/${organizationId}/${jobId}?search.deleted_eq=true">
                <i class="fa fa-table"></i>
               <fmt:message key="userList.title"/>
            </a>
        </li>
        <li ${param['search.status_eq'] eq 'blocked' ? 'class="active"' : ''}>
            <a href="${ctx}/sys/users/${organizationId}/${jobId}?search.status_eq=blocked">
                <i class="fa fa-table"></i>
               <fmt:message key="userList.title"/>
            </a>
        </li>
    </ul>

    <sys:showMessage/>


    <div class="row-fluid tool ui-toolbar">
        <div class="span3">
            <div class="btn-group">
                <secure:hasPermission name="sys:user:add">
                <a class="btn no-disabled btn-create">
                    <span class="icon-file-alt"></span>
                    <fmt:message key="button.add"/>
                </a>
                </secure:hasPermission>
                <secure:hasPermission name="sys:user:edit">
                <a id="update" class="btn btn-update">
                    <span class="fa fa-edit"></span>
                    <fmt:message key="button.edit"/>
                </a>
                </secure:hasPermission>
                <secure:hasPermission name="sys:user:delete">
                <a class="btn btn-delete">
                    <span class="fa fa-trash"></span>
                    <fmt:message key="button.edit"/>
                </a>
                </secure:hasPermission>
                <secure:hasPermission name="sys:user;*"><%-- The following actions are available when user owner full right in user page --%>
                <div class="btn-group last">
                    <a class="btn dropdown-toggle" data-bs-toggle="dropdown" href="#">
                        <i class="icon-wrench"></i>
                        <fmt:message key="button.moreactions"/>
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a class="btn btn-link change-password">
                                <i class="icon-key"></i>
                                <fmt:message key="updatePassword.changePasswordButton"/>
                            </a>
                        </li>
                        <li>
                            <a class="btn btn-link block-user">
                                <i class="icon-lock"></i>
                                <fmt:message key="updatePassword.lockuser"/>
                            </a>
                        </li>
                        <li>
                            <a class="btn btn-link unblocked-user">
                                <i class="icon-unlock"></i>
                                <fmt:message key="updatePassword.unlockuser"/>
                            </a>
                        </li>
                        <li>
                            <a class="btn btn-link recycle">
                                <i class="fa fa-check"></i>
                                <fmt:message key="updatePassword.restoreuser"/>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a class="btn btn-link status-history">
                                <i class="fa fa-table"></i>
                                <fmt:message key="updatePassword.userstatushistory"/>
                            </a>
                        </li>
                        <li>
                            <a class="btn btn-link last-online-info">
                                <i class="fa fa-table"></i>
                                <fmt:message key="updatePassword.userlastonline"/>
                            </a>
                        </li>
                    </ul>
                </div>
                </secure:hasPermission>
            </div>
        </div>
        <div class="span9">
            <%@include file="searchForm.jsp" %>
        </div>
    </div>
    <table id="table" class="sort-table table table-bordered table-hover" data-prefix-url="${ctx}/sys/users">
        <thead>
        <tr>
            <th style="width: 20px;">&nbsp;</th>
            <th style="width: 80px;">
                <a class="check-all" href="javascript:;"><fmt:message key="button.selectall"/></a>
                |
                <a class="reverse-all" href="javascript:;"><fmt:message key="button.invertselection"/></a>
            </th>
            <th sort="id"><fmt:message key="user.id"/></th>
            <th sort="username"><fmt:message key="user.username"/></th>
            <th sort="email"><fmt:message key="user.email"/></th>
            <th sort="mobilePhoneNumber"><fmt:message key="user.mobile"/></th>
            <th><fmt:message key="user.dateAdded"/></th>
            <th><fmt:message key="user.userstatus"/></th>
            <th><fmt:message key="user.usertype"/></th>
        </tr>
        <tbody>
        <c:forEach items="${page.content}" var="m">
            <tr>
                <td>
                    <a data-id="${m.id}"
                       class="btn-link toggle-child icon-plus-sign"
                       title="<fmt:message key="userList.viewcompanyinfo"/>">
                    </a>
                </td>

                <td class="check">
                    <input type="checkbox" name="ids" value="${m.id}" data-status="${m.status}"
                           data-deleted="${m.deleted}"/>
                </td>
                <td>
                    <a href="${ctx}/sys/users/${m.id}">
                        ${m.id}
                    </a>
                </td>
                <td>${m.username}</td>
                <td>${m.email}</td>
                <td>${m.mobile}</td>
                <td><spring:eval expression="m.dateAdded"/></td>
                <td>${m.status.info}</td>
                <td>${m.userType eq 0 ?'Yes' : 'No'}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <sys:page page="${page}"/>
</div>

