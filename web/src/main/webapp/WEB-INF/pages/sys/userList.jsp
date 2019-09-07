<%@ include file="/common/taglibs.jsp" %>

<head>
    <title><fmt:message key="userList.title"/></title>
    <meta name="menu" content="AdminMenu"/>
</head>

<c:set var="group" value="grp_boostrap_table_treegrid" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/userList.js"%>
</c:set>

<c:if test="${not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="${searchError}"/>
    </div>
</c:if>

<div class="col-sm-10">
    <h2><fmt:message key="userList.heading"/></h2>

<%--     <form method="get" action="${ctx}/sys/users" id="searchForm" class="form-inline">
    <div id="search" class="text-right">
        <span class="col-sm-9">
            <input type="text" size="20" name="q" id="query" value="${param.q}"
                   placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm">
        </span>
        <button id="button.search" class="btn btn-default btn-sm" type="submit">
            <i class="fa fa-search"></i> <fmt:message key="button.search"/>
        </button>
    </div>
    </form> --%>
    
    <form id="searchForm" class="form-inline float-right">
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

    <div id="actions" class="btn-group">
    	<secure:hasPermission name="sys:users:add">
        <a class="btn btn-primary" href="<c:url value='/sys/userform?method=Add&from=list'/>">
            <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
		</secure:hasPermission>
		<secure:hasPermission name="sys:users:delete">
        <button class="btn btn-danger" id="delete" name="delete">
	        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
	    </secure:hasPermission>
	    <secure:hasPermission name="sys:users:data_import">
	    <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
	        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
	    </secure:hasPermission>
	    <secure:hasPermission name="sys:users:data_export">
	    <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
	        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
	    </secure:hasPermission>
	    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
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
	            <th data-field="username" data-formatter="nameFormatter"><fmt:message key="user.username"/></th>
		        <th data-field="fullName"><fmt:message key="activeUsers.fullName"/></th>       
		        <th data-field="email"><fmt:message key="user.email"/></th>        
		        <th data-field="enabled"><fmt:message key="user.enabled"/></th>
		        <th data-field="organizationCode"><fmt:message key="user.organization"/></th>
	        </tr>
	        </thead>
	    </table>
	</div>
</div>
