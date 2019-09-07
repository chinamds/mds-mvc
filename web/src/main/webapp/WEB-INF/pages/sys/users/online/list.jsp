<%@ include file="/common/taglibs.jsp"%>

<c:if test="${empty header['container']}">
<head>
	<title><fmt:message key="onlineUsers.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/online/list.js"%>
</c:set>

<style type="text/css">
    .scroll-pane {
        float: left;
        width: 100%;
        height: 100%;
        overflow: auto;
    }
</style>
</c:if>
<div data-table="table" class="panel" id="panel">

    <ul class="nav nav-tabs">
        <li ${empty param['search.userId_eq'] and empty param['search.userId_gt'] ? 'class="active"' : ''}>
            <a href="${ctx}/admin/sys/users/online">
                <i class="fa fa-table"></i>
               <fmt:message key="onlineUsers.allonlineusesr"/>
            </a>
        </li>

        <li ${not empty param['search.userId_gt'] ? 'class="active"' : ''}>
            <a href="${ctx}/admin/sys/users/online?search.userId_gt=0">
                <i class="fa fa-table"></i>
                <fmt:message key="onlineUsers.loginusers"/>
            </a>
        </li>
        <li ${not empty param['search.userId_eq'] ? 'class="active"' : ''}>
            <a href="${ctx}/admin/sys/users/online?search.userId_eq=0">
                <i class="fa fa-table"></i>
                <fmt:message key="onlineUsers.guests"/>
            </a>
        </li>
    </ul>

    <div class="row-fluid tool ui-toolbar">
        <div class="span4">
            <div class="btn-group">
                <secure:hasPermission name="sys:userOnline or monitor:userOnline"><%-- be equivalent to sys:userOnline:* full right --%>
                <a class="btn btn-force-logout">
                    <span class="icon-lightbulb"></span>
                    <fmt:message key="onlineUsers.kickout"/>
                </a>
                </secure:hasPermission>
            </div>

        </div>
        <div class="span8">
            <%@include file="searchForm.jsp" %>
        </div>
    </div>
    <%@include file="listTable.jsp"%>
</div>
