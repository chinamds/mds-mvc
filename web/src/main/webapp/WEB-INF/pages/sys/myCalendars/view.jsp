<%@ include file="/common/taglibs.jsp"%>

<%-- <page:applyDecorator name="default_dlg">
<head>
	<title><fmt:message key="myCalendarDetail.title"/></title>
</head>

<c:set var="group" value="grp_fullcalendar" scope="request" /> class="form-horizontal" --%>

<div class="col">
	<form>
	    <div class="form-group">
	        <label class="control-label"><fmt:message key="myCalendar.title"/></label>
	        <div title="${calendar.title}">
		      <p class="form-control-static">${calendar.title}</p>
		    </div>
	    </div>
	    <div class="form-group">
	        <label class="control-label"><fmt:message key="myCalendar.startDate"/></label>
	        <div>
	            <fmt:formatDate value="${calendar.startDate}" pattern="yyyy-MM-dd"/>
	            <c:if test="${not empty calendar.startTime}">
	                &nbsp;<fmt:formatDate value="${calendar.startTime}" pattern="HH:mm:ss"/>
	            </c:if>
	        </div>
	    </div>
	    <div class="form-group">
	        <label class="control-label"><fmt:message key="myCalendar.endTime"/></label>
	        <div>
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
	        <label class="control-label"><fmt:message key="myCalendar.details"/></label>
	        <div title="${calendar.details}">
		        <p class="form-control-static">${calendar.details}</p>
	        </div>
	    </div>
	    
	    <div class="form-group">
            <button type="button" class="btn btn-danger" id="delete" name="delete">
                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
            </button>
	
	        <button type="button" class="btn btn-default" id="cancel" name="cancel">
	            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
	        </button>
	    </div>
	</form>
</div>
<%-- </page:applyDecorator> --%>