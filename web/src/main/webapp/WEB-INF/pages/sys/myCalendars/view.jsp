<%@ include file="/common/taglibs.jsp"%>

<page:applyDecorator name="default_dlg">
<head>
	<title><fmt:message key="myCalendarDetail.title"/></title>
</head>

<c:set var="group" value="grp_fullcalendar" scope="request" />

<div id='calendar' class="col-sm-10">
	<form class="form-horizontal">
	    <div class="form-group">
	        <label class="col-sm-4 control-label"><fmt:message key="myCalendar.title"/></label>
	        <div class="col-sm-8" title="${calendar.title}">
		      <p class="form-control-static">${calendar.title}</p>
		    </div>
	    </div>
	    <div class="form-group">
	        <label class="col-sm-4 control-label"><fmt:message key="myCalendar.startDate"/></label>
	        <div class="col-sm-8">
	            <fmt:formatDate value="${calendar.startDate}" pattern="yyyy-MM-dd"/>
	            <c:if test="${not empty calendar.startTime}">
	                &nbsp;<fmt:formatDate value="${calendar.startTime}" pattern="HH:mm:ss"/>
	            </c:if>
	        </div>
	    </div>
	    <div class="form-group">
	        <label class="col-sm-4 control-label"><fmt:message key="myCalendar.endTime"/></label>
	        <div class="col-sm-8">
	            <fmt:formatDate value="${calendar.endDate}" pattern="yyyy-MM-dd"/>
	            <c:if test="${not empty calendar.endTime}">
	                &nbsp;<fmt:formatDate value="${calendar.endTime}" pattern="HH:mm:ss"/>
	            </c:if>
	            <c:if test="${empty calenadr.startTime and empty calendar.endTime}">
	                	&nbsp;(<fmt:message key="myCalendar.allday"/>)
	            </c:if>
	        </div>
	    </div>
	    <div class="form-group">
	        <label class="col-sm-4 control-label"><fmt:message key="myCalendar.details"/></label>
	        <div class="col-sm-8" title="${calendar.details}">
		        <p class="form-control-static">${calendar.details}</p>
	        </div>
	    </div>
	</form>
</div>
</page:applyDecorator>