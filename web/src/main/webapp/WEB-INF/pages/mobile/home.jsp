<%@ include file="/common/taglibs.jsp"%>

<page:applyDecorator name="default_login">
<head>
	<title><fmt:message key="welcome.title"/></title>
</head>

<c:set var="group" value="grp_fullcalendar" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/welcome.js"%>
</c:set>

<body class="tab">
<div style="margin-top: 10px;">
    <div class="navbar navbar-default">
        <div style="padding-top: 5px;">
            <a class="btn btn-link btn-view-info" data-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.welcome.tip"/>">
                <secure:principal property="loginName"/><fmt:message key="menu.welcome"/>
            </a>
            <span class="muted">|</span>
            &nbsp;
            <span class="muted">
                <fmt:message key="home.youhave"/>
                <a class="btn btn-link btn-view-message no-padding" data-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.viewunreadmessages"/>">
                    <span class="badge badge-important">${messageUnreadCount}</span>
                </a>
                <fmt:message key="home.unreadmessages"/>
            </span>
        </div>
    </div>
    <br/>
    <fieldset>
        <legend>
            <fmt:message key="home.mycalendar"/>
            (<span class="badge badge-important" data-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.lastthreedays.reminder"><fmt:param>${calendarCount}</fmt:param></fmt:message>">${calendarCount}</span>)
            <i class="fa fa-angle-double-down"></i>
        </legend>
        <div id='calendar'></div>
    </fieldset>
    <br/>
    <br/>
    <br/>
</div>
</body>
</page:applyDecorator>