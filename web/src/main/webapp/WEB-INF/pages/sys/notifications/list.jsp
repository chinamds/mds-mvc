<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="notificationList.title"/></title>
</head>

<c:set var="group" value="grp_personal" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/notifications/list.js"%>
</c:set>

<div class="col">
<div data-table="table" class="panel"  data-state="${state}" data-state-info="${state.info}">

    <ul class="nav nav-tabs">
        <li class="nav-item active">
            <a class="nav-link" href="${ctx}/sys/notifications">
                <i class="fa fa-table"></i>
               <fmt:message key="menu.mynotifications"/>
            </a>
        </li>
    </ul>
    
    <div class="table-responsive">
		<table id="table">
	        <thead>
	        <tr>
	            <th data-field="state" data-checkbox="true"></th>
	            <%-- <th data-field="id"
	                data-align="center"
	                data-formatter="actionFormatter"
	                data-events="actionEvents"><fmt:message key="table.operation"/></th> --%>
	            <th data-field="source" data-formatter="titleFormatter"><fmt:message key="notification.source"/></th>
		        <th data-field="date" data-formatter="dateFormatter"><fmt:message key="notification.date"/></th>
		        <th data-field="content"><fmt:message key="notification.content"/></th>
	        </tr>
	        </thead>
	    </table>
	</div>
</div>
</div>