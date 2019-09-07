<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="userStatusHistory.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/list.js"%>
</c:set>

<div data-table="historyTable" class="panel">

    <ul class="nav nav-tabs">
        <li class="active">
            <a href="${ctx}/admin/sys/users/statusHistory">
                <i class="fa fa-table"></i>
                <fmt:message key="userStatusHistory.title"/>
            </a>
        </li>
    </ul>

    <div class="row-fluid tool ui-toolbar">
        <%@include file="searchForm.jsp" %>
    </div>
    <%@include file="listTable.jsp"%>
</div>