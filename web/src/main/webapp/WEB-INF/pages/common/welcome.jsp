<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="welcome.title"/></title>
</head>

<c:set var="group" value="grp_fullcalendar" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/welcome.js"%>
</c:set>

<body class="welcome">
<div style="margin-top: 8px;">
    <div class="navbar navbar-default navbar-light bg-light py-0 mb-3" role="navigation">
    	<div class="py-0 my-0">
	        <a class="btn btn-link btn-view-info" data-bs-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.welcome.tip"/>">
	            ${pageContext.request.remoteUser}<fmt:message key="menu.welcome"/>
	        </a>
	        <span class="muted">|</span>
	        &nbsp;
	        <span class="muted">
	            <fmt:message key="home.youhave"/>
	            <a class="btn btn-link btn-view-message no-padding" data-bs-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.viewunreadmessages"/>">
	                <span class="badge badge-important badge-pill badge-info">${messageUnreadCount}</span>
	            </a>
	            <fmt:message key="home.unreadmessages"/>
	        </span>
        </div>
    </div>
    <fieldset class="mds-fieldset">
        <legend>
            <fmt:message key="home.mycalendar"/>
            (<span class="badge badge-important badge-pill badge-info" data-bs-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.lastthreedays.reminder"><fmt:param>${calendarCount}</fmt:param></fmt:message>">${calendarCount}</span>)
            <i class="fa fa-angle-double-down"></i>
        </legend>
       	<div id='calendar'></div>
    </fieldset>
    <br/>
    <br/>
    <br/>
</div>
<v:javascript formName="myCalendar" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
</body>