<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="notificationList.title"/></title>
    <meta name="menu" content="NotificationMenu"/>
</head>

<c:set var="group" value="grp_personal" scope="request" />
<c:set var="scripts" scope="request">
    <%@ include file="/static/scripts/sys/notifications/list.js"%>
</c:set>

<div class="col">
	<c:if test="{'$'}{not empty searchError}">
	    <div class="alert alert-danger alert-dismissable">
	        <a href="#" data-dismiss="alert" class="close">&times;</a>
	        <c:out value="{'$'}{searchError}"/>
	    </div>
	</c:if>
	
	<h2><fmt:message key="notificationList.heading"/></h2>
	
	<form id="searchForm" class="form-inline float-end">
        <div id="search" class="input-group">
             <input type="text" size="20" name="q" id="query" value="${param.q}"
                       placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
            <span class="input-group-append">
                <button id="buttonSearch" class="btn btn-default btn-sm">
                    <i class="fa fa-search"></i> <fmt:message key="button.search"/>
                </button>
            </span>
        </div>
    </form>
	
	<p><fmt:message key="notificationList.message"/></p>
	
	<div id="actions" class="btn-group">
	    <a href='<c:url value="/notificationform"/>' class="btn btn-primary">
	        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
	    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
	</div>
	
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