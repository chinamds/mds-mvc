<%@ include file="/common/taglibs.jsp"%>

<page:applyDecorator name="default_dlg">
<head>
	<title><fmt:message key="myCalendarDetail.title"/></title>
</head>

<c:set var="group" value="grp_fullcalendar" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/myCalendars/newForm.js"%>
</c:set>

<div class="col-sm-10">
	<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
    <form:form id="editForm" action="editForm" method="post" modelAttribute="myCalendar" cssClass="form-horizontal"  onsubmit="return validateForm(this)">
    <form:hidden path="id"/>  
        <spring:bind path="myCalendar.title">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''} row">
	    </spring:bind>
            <%-- <form:label path="title" cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.title"/></form:label>
            <form:input path="title" cssClass="validate[required,maxSize[100]]"/> --%>
            <appfuse:label key="myCalendar.title" styleClass="col-sm-4 control-label"/>
            <div class="col-sm-8">
		        <form:input cssClass="form-control" path="title" id="title"  maxlength="255" />
		        <form:errors path="title" cssClass="help-block"/>
		    </div>
        </div>

		<spring:bind path="myCalendar.startDate">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''} row">
	    </spring:bind>        
        	<appfuse:label key="myCalendar.startDate" styleClass="col-sm-4 control-label"/>
            <%-- <form:label path="startDate"  cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.startDate"/></form:label> --%>
            <div class="col-sm-8">
	            <div class="input-group date" id="datetimepicker0" data-target-input="nearest">
	                <form:input path="startDate" cssClass="form-control datetimepicker-input" data-target="#datetimepicker0" data-position="bottom-left" data-format="${fn:toUpperCase(datePattern)}"/>
	                <!-- <span class="add-on"><i data-time-icon="fa fa-time" data-date-icon="fa fa-calendar"></i></span> -->
	                <div class="input-group-append input-group-addon" data-target="#datetimepicker0" data-toggle="datetimepicker">
                        <div class="input-group-text"><i class="fa fa-calendar icon-calendar"></i></div>
                    </div>
	                <form:errors path="startDate" cssClass="help-block"/>
	            </div>
            </div>
        </div>

		<spring:bind path="myCalendar.duration">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''} row">
	    </spring:bind>        
        	<appfuse:label key="myCalendar.duration" styleClass="col-sm-4 control-label"/>
            <%-- <form:label path="duration"  cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.duration"/></form:label> --%>
            <div class="col-sm-4">
	            <form:input path="duration" cssClass="form-control input-small"/>
	            <form:errors path="duration" cssClass="help-block"/>
            </div>
            <div class="checkbox col-sm-4">
			    <label>
			      <input type="checkbox" class="all-day"><fmt:message key="myCalendar.allday"/></label>
			</div>
        </div>

		<spring:bind path="myCalendar.startTime">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''} row">
	    </spring:bind>        
        	<appfuse:label key="myCalendar.startTime" styleClass="col-sm-4 control-label"/>
            <%-- <form:label path="startTime" cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.startTime" data-format="HH:mm:ss"/></form:label> --%>
            <div class="col-sm-8">
	            <div class="input-group date" id="datetimepicker1" data-target-input="nearest">
		            <form:input path="startTime" cssClass="form-control datetimepicker-input" data-target="#datetimepicker1" data-position="bottom-left" data-format="HH:mm"/>
		            <div class="input-group-append input-group-addon" data-target="#datetimepicker1" data-toggle="datetimepicker">
                        <div class="input-group-text"><i class="fa fa-clock icon-calendar"></i></div>
                    </div>
		            <form:errors path="startTime" cssClass="help-block"/>
	            </div>
            </div>
        </div>

		<spring:bind path="myCalendar.endTime">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''} row">
	    </spring:bind>        
        	<appfuse:label key="myCalendar.endTime" styleClass="col-sm-4 control-label"/>
            <%-- <form:label path="endTime"  cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.endTime"/></form:label> --%>
            <div class="col-sm-8">
	            <div class="input-group date" id="datetimepicker2" data-target-input="nearest">
	                <form:input path="endTime" cssClass="form-control datetimepicker-input" data-target="#datetimepicker2" data-position="bottom-left" data-format="HH:mm"/>
	                <div class="input-group-append input-group-addon" data-target="#datetimepicker2" data-toggle="datetimepicker">
                        <div class="input-group-text"><i class="fa fa-clock icon-calendar"></i></div>
                    </div>
	            	<form:errors path="endTime" cssClass="help-block"/>
            	</div>
            </div>
        </div>


		<spring:bind path="myCalendar.details">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''} row">
	    </spring:bind>        
        	<appfuse:label key="myCalendar.details" styleClass="col-sm-4 control-label"/>
            <%-- <form:label path="details"  cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.details"/></form:label> --%>
            <div class="col-sm-8">
            	<form:textarea path="details" cssClass="form-control"/>
            	<form:errors path="details" cssClass="help-block"/>
            </div>
        </div>


		<spring:bind path="myCalendar.backgroundColor">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''} row">
	    </spring:bind>        
        	<appfuse:label key="myCalendar.backgroundColor" styleClass="col-sm-4 control-label"/>
            <%-- <form:label path="backgroundColor" cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.backgroundColor"/></form:label> --%>
            <div class="col-sm-8">
	            <%-- <select id="backgroundColor" name="backgroundColor" class="form-control" style="background: ${backgroundColorList[0]}">
	                <c:forEach items="${backgroundColorList}" var="c">
	                    <option style="background: ${c}" value="${c}">&nbsp;</option>
	                </c:forEach>
	            </select> --%>
	            <form:input cssClass="form-control" path="backgroundColor" id="backgroundColor" />
	            <form:errors path="backgroundColor" cssClass="help-block"/>
            </div>
            <%--<form:label path="textColor" cssStyle="width: 60px;text-align: right;"><fmt:message key="myCalendar.textColor"/></form:label>--%>
            <%--<form:input path="textColor" cssClass="input-small"/>--%>
        </div>
    </form:form>
</div>

<v:javascript formName="myCalendar" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<!-- <script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['editForm']).focus();
    });
</script> -->
</page:applyDecorator>
