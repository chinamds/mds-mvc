<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="lastOnlineUsers.title"/></title>
</head>

<div data-table="historyTable" class="panel">

    <ul class="nav nav-tabs">
        <li class="active">
            <a href="${ctx}/sys/users/lastOnline">
                <i class="fa fa-table"></i>
               <fmt:message key="lastOnlineUsers.title"/>
            </a>
        </li>
    </ul>

    <div class="row-fluid tool ui-toolbar">
        <%@include file="searchForm.jsp" %>
    </div>
    <%@include file="listTable.jsp"%>
</div>
