<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="myCalendarList.title"/></title>
</head>

<c:set var="group" value="grp_fullcalendar" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/myCalendars/list.js"%>
</c:set>

<style>
    body {
        margin-top: 40px;
        text-align: center;
        font-size: 14px;
        font-family: "Lucida Grande",Helvetica,Arial,Verdana,sans-serif;
    }

    .fc-button-add {
        margin-right: 10px!important;
    }

    #loading {
        position: absolute;
        top: 5px;
        right: 5px;
    }

    .ui-dialog {
        overflow: visible!important;
    }
    .ui-dialog-content {
        overflow: hidden!important;
        overflow: visible!important;
    }

    #calendar {
        width: 800px;
        margin: 0 auto;
    }
</style>

<div id='calendar'></div>
