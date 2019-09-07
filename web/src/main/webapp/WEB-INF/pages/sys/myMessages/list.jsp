<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.sys.model.MessageFolder" %>

<head>
	<title><fmt:message key="myMessageList.title"/></title>
</head>

<c:set var="group" value="grp_personal" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/myMessages/list.js"%>
</c:set>
<c:set var="stateInfo" scope="request"><fmt:message key="${state.info}"/></c:set>

<div class="col">
<div data-table="table" data-state="${state}" data-state-info="${stateInfo}">
    <ul class="nav nav-tabs">
    	<li class="nav-item" role="presentation">
            <a class="nav-link" href="${ctx}/sys/messageform/send">
                <i class="fa fa-file"></i>
                <fmt:message key="myMessage.messageoperate.newmessage"/>
            </a>
        </li>
<c:if test="${fns:isMobileDevice(pageContext.request)}">
		<li role="presentation" class="nav-item">
            <a class="nav-link ${MessageFolder.inbox eq state ? 'active' : ''}" href="${ctx}/sys/myMessages/${MessageFolder.inbox}/list">
                <i class="fas fa-inbox"></i>
                <fmt:message key="${MessageFolder.inbox.info}"/>
            </a>
        </li>        
        <li role="presentation" class="dropdown nav-item">
		    <a class="nav-link dropdown-toggle ${MessageFolder.inbox ne state ? 'active' : ''}" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
		      <fmt:message key="myMessage.messagefolder.more"/><span class="caret"></span>
		    </a>
		    <ul class="dropdown-menu">
				<c:forEach items="${states}" var="s">
		            <c:if test="${s ne MessageFolder.deleted and s ne MessageFolder.inbox}">
		                <a class="dropdown-item ${s eq state ? 'active' : ''}" href="${ctx}/sys/myMessages/${s}/list">
		                    <i class="${s.icon}"></i>
		                    <fmt:message key="${s.info}"/>
		                </a>
		            </c:if>
		        </c:forEach>
		    </ul>
		</li>
</c:if>        
<c:if test="${not fns:isMobileDevice(pageContext.request)}">        
        <c:forEach items="${states}" var="s">
            <c:if test="${s ne MessageFolder.deleted}">
	            <li class="nav-item">
	                <a class="nav-link ${s eq state ? 'active' : ''}" href="${ctx}/sys/myMessages/${s}/list">
	                    <i class="${s.icon}"></i>
	                    <fmt:message key="${s.info}"/>
	                </a>
	            </li>
            </c:if>
        </c:forEach>
</c:if>        
    </ul>

    <sys:showMessage/>

<div class="btn-group" role="group">
    <%-- <a role="button" class="btn navbar-btn btn-sm no-disabled" href="${ctx}/sys/messageform/send">
        <i class="fa fa-file-o"></i>
        <fmt:message key="myMessage.messageoperate.newmessage"/>
    </a> --%>
    
    <c:if test="${state ne MessageFolder.drafts and state ne MessageFolder.archive and state ne MessageFolder.junk}">
        <a role="button" class="btn btn-sm btn-archive">
            <i class="fa fa-briefcase"></i>
            <fmt:message key="myMessage.messageoperate.archive"/>
        </a>
    </c:if>

    <c:if test="${state eq MessageFolder.junk}">
        <a role="button" class="btn btn-sm btn-archive">
            <i class="fa fa-briefcase"></i>
            <fmt:message key="myMessage.messageoperate.movetoarchive"/>
        </a>
    </c:if>

    <a role="button" class="btn btn-sm btn-recycle-or-delete">
        <i class="fa fa-trash"></i>
       <fmt:message key="myMessage.messageoperate.delete"/>
    </a>

    <c:if test="${state eq MessageFolder.inbox}">
		<c:if test="${fns:isMobileDevice(pageContext.request)}">    
		    <div class="btn-group" role="group">
			    <a type="button" class="btn btn-sm btn-more dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			      	......<span class="caret"></span>
			    </a>
			    <ul class="dropdown-menu">
			      <li><a href="#"><i class="fa fa-bookmark"></i>
		           	<fmt:message key="myMessage.messageoperate.markasread"/></a></li>
			      <li><a href="#"><i class="fa fa-eraser"></i>
		        	<fmt:message key="myMessage.messageoperate.emptyfolder"/></a></li>
			    </ul>
			  </div>
		</c:if>	  
		<c:if test="${not fns:isMobileDevice(pageContext.request)}">
		        <a role="button" class="btn btn-sm btn-mark-read">
		            <i class="fa fa-bookmark"></i>
		           <fmt:message key="myMessage.messageoperate.markasread"/>
		        </a>
		</c:if>        
    </c:if>

	<c:if test="${state ne MessageFolder.inbox or (state eq MessageFolder.inbox and not fns:isMobileDevice())}">
	    <a role="button" class="btn btn-sm no-disabled btn-clear">
	        <i class="fa fa-eraser"></i>
	        <fmt:message key="myMessage.messageoperate.emptyfolder"/><%-- ${state.info} --%>
	    </a>
    </c:if>
</div>
<form id="searchForm" class="form-inline float-right">
<div id="search-message" class="input-group">
	 <input type="text" size="20" name="q" id="query" value="${param.q}"
               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
    <span class="input-group-append">
		<button id="buttonSearch" class="btn btn-default btn-sm">
	        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
		</button>
	</span>
</div>
</form>
<c:if test="${state eq MessageFolder.inbox or state eq MessageFolder.outbox}">
    <p class="text-warning small"><fmt:message key="myMessage.clearmessage.tip"><fmt:param value="${stateInfo}"/></fmt:message></p>
</c:if>
<c:if test="${state eq MessageFolder.junk}">
    <p class="text-warning small"><fmt:message key="myMessage.clearjunkfolder.tip"/></p>
</c:if>

<%-- <div class="span8 muted" style="text-align: right;line-height: 31px;padding-right: 10px;">
    <c:if test="${state eq 'inbox' or state eq 'outbox'}">
     <fmt:message key="myMessage.clearmessage.tip"><fmt:param value="${state.info}"/></fmt:message>
    </c:if>
    <c:if test="${state eq 'junk'}">
        <fmt:message key="myMessage.clearjunkfolder.tip"/>${state.info}
    </c:if>
</div> --%>

 <%@include file="listTable.jsp"%>

</div>
</div>
